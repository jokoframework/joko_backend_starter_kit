#!/usr/bin/env bash
#
# turn-key.sh — Deja el joko_backend_starter_kit listo para ejecutar sin
# Personal Access Token (PAT) de GitHub.
#
#   1. SDKMAN + Java 21 + Maven (si no están instalados)
#   2. Si faltan en ~/.m2 las versiones del pom, instala joko-utils
#      (tag público) y/o el parent joko-security 2.x (hermano ../security)
#   3. Deja configurado .env para docker compose
#   4. Verifica que el proyecto compile
#
# Uso:   ./scripts/turn-key.sh
# Layout esperado (directorios hermanos):
#   <parent>/
#     security/                    # joko-security-parent 2.x
#     joko_backend_starter_kit/    # este repo
# Variables de entorno (opcionales):
#   JOKO_SRC_DIR         dónde clonar joko-utils (default: ~/git/jokoframework)
#   JOKO_SECURITY_SRC    override del parent 2.x (default: ../security)

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
M2_DIR="${HOME}/.m2"
JOKO_SRC_DIR="${JOKO_SRC_DIR:-${HOME}/git/jokoframework}"
JOKO_SECURITY_SRC="${JOKO_SECURITY_SRC:-${REPO_ROOT}/../security}"
POM="${REPO_ROOT}/pom.xml"
JOKO_UTILS_VERSION="$(sed -n 's/.*<joko-utils.version>\([^<]*\)<\/joko-utils.version>.*/\1/p' "$POM" | head -n1)"
JOKO_SECURITY_VERSION="$(sed -n 's/.*<joko-security.version>\([^<]*\)<\/joko-security.version>.*/\1/p' "$POM" | head -n1)"
JOKO_UTILS_TAG="v${JOKO_UTILS_VERSION}"

m2_artifact() {
    local group_path="$1" artifact="$2" version="$3" ext="${4:-jar}"
    local f="${M2_DIR}/repository/${group_path}/${artifact}/${version}/${artifact}-${version}.${ext}"
    [ -f "$f" ]
}

joko_utils_in_m2() {
    m2_artifact "io/github/jokoframework" "joko-utils" "$JOKO_UTILS_VERSION" jar
}

joko_security_in_m2() {
    m2_artifact "io/github/jokoframework" "joko-security-starter" "$JOKO_SECURITY_VERSION" jar \
        && m2_artifact "io/github/jokoframework" "joko-security-parent" "$JOKO_SECURITY_VERSION" pom \
        && m2_artifact "io/github/jokoframework" "joko-security-core" "$JOKO_SECURITY_VERSION" jar
}

say()  { printf '\n\033[1;36m==> %s\033[0m\n' "$*"; }
warn() { printf '\033[1;33m[!] %s\033[0m\n' "$*"; }

if sed --version >/dev/null 2>&1; then SED_IN=(-i); else SED_IN=(-i ''); fi

if [ ! -d "${HOME}/.sdkman" ]; then
    say "Instalando SDKMAN..."
    command -v curl >/dev/null 2>&1 || { echo "ERROR: 'curl' es obligatorio." >&2; exit 1; }
    curl -s "https://get.sdkman.io" | bash
fi

# shellcheck disable=SC1091
set +u
source "${HOME}/.sdkman/bin/sdkman-init.sh"
set -u

SDKMAN_CONFIG="${HOME}/.sdkman/etc/config"
if ! grep -q '^sdkman_auto_answer=true' "$SDKMAN_CONFIG" 2>/dev/null; then
    printf '\nsdkman_auto_answer=true\n' >> "$SDKMAN_CONFIG"
fi

sdk_call() {
    ( set +u; sdk "$@" )
}

JAVA_MAJOR="$(java -version 2>&1 | awk -F[\".] '/version/ {print $2; exit}')"
if [ -z "${JAVA_MAJOR}" ] || [ "${JAVA_MAJOR}" -lt 21 ]; then
    JAVA_CANDIDATE="$(sdk_call list java 2>/dev/null \
        | perl -pe 's/\e\[[0-9;]*m//g' \
        | grep -oE '21\.[0-9]+\.[0-9]+-tem' | head -n1 || true)"
    JAVA_CANDIDATE="${JAVA_CANDIDATE:-21.0.8-tem}"
    if [ ! -d "${HOME}/.sdkman/candidates/java/${JAVA_CANDIDATE}" ]; then
        say "Instalando Java ${JAVA_CANDIDATE} (SDKMAN)..."
        sdk_call install java "$JAVA_CANDIDATE"
    fi
    export JAVA_HOME="${HOME}/.sdkman/candidates/java/${JAVA_CANDIDATE}"
    sdk_call default java "$JAVA_CANDIDATE" >/dev/null 2>&1 || true
    export PATH="${JAVA_HOME}/bin:${PATH}"
else
    say "Java ${JAVA_MAJOR} ya satisface el mínimo (21)."
fi

if ! command -v mvn >/dev/null 2>&1; then
    MAVEN_CANDIDATE="$(sdk_call list maven 2>/dev/null \
        | perl -pe 's/\e\[[0-9;]*m//g' \
        | grep -oE '3\.9\.[0-9]+' | sort -V -r | head -n1 || true)"
    MAVEN_CANDIDATE="${MAVEN_CANDIDATE:-3.9.16}"
    if [ ! -d "${HOME}/.sdkman/candidates/maven/${MAVEN_CANDIDATE}" ]; then
        say "Instalando Maven ${MAVEN_CANDIDATE} (SDKMAN)..."
        sdk_call install maven "$MAVEN_CANDIDATE"
    fi
    export PATH="${HOME}/.sdkman/candidates/maven/${MAVEN_CANDIDATE}/bin:$PATH"
    sdk_call default maven "$MAVEN_CANDIDATE" >/dev/null 2>&1 || true
fi

"$JAVA_HOME/bin/java" -version 2>&1 | head -n1 || java -version 2>&1 | head -n1
mvn -v 2>/dev/null | head -n1 || true

if [ -z "$JOKO_UTILS_VERSION" ] || [ -z "$JOKO_SECURITY_VERSION" ]; then
    echo "ERROR: no se leyeron joko-utils.version / joko-security.version de ${POM}" >&2
    exit 1
fi

say "Versiones del pom: joko-utils ${JOKO_UTILS_VERSION}, joko-security ${JOKO_SECURITY_VERSION}"

build_install() {
    local project_dir="$1"
    say "Compilando e instalando $(basename "$project_dir") en ~/.m2 ..."
    ( cd "$project_dir" && mvn -B -q install -DskipTests -Ddependency-check.skip=true )
}

clone_and_checkout() {
    local url="$1" dir="$2" tag="$3"
    if [ ! -d "${dir}/.git" ]; then
        say "Clonando ${url}..."
        git clone --quiet "$url" "$dir"
    else
        say "$(basename "$dir") ya está clonado."
    fi
    git -C "$dir" fetch --quiet --tags origin 2>/dev/null || true
    git -C "$dir" checkout --quiet "$tag"
    echo "  -> $(basename "$dir") en $(git -C "$dir" describe --tags 2>/dev/null || echo "$tag")"
}

if joko_utils_in_m2; then
    say "joko-utils ${JOKO_UTILS_VERSION} ya está en ~/.m2; no se clona ni se compila."
else
    mkdir -p "$JOKO_SRC_DIR"
    clone_and_checkout "https://github.com/jokoframework/joko-utils.git" \
        "${JOKO_SRC_DIR}/joko-utils" "$JOKO_UTILS_TAG"
    build_install "${JOKO_SRC_DIR}/joko-utils"
fi

if joko_security_in_m2; then
    say "joko-security ${JOKO_SECURITY_VERSION} ya está en ~/.m2; no se compila el hermano ../security."
else
    if [ ! -f "${JOKO_SECURITY_SRC}/pom.xml" ]; then
        echo "ERROR: no está el parent joko-security 2.x en ${JOKO_SECURITY_SRC}" >&2
        echo "Esperado: directorio hermano ../security (junto a joko_backend_starter_kit)." >&2
        echo "Override: JOKO_SECURITY_SRC=/ruta/al/security ./scripts/turn-key.sh" >&2
        exit 1
    fi
    if ! grep -q 'joko-security-parent' "${JOKO_SECURITY_SRC}/pom.xml"; then
        echo "ERROR: ${JOKO_SECURITY_SRC} no es el parent modular 2.x (joko-security-parent)." >&2
        exit 1
    fi
    JOKO_SECURITY_SRC="$(cd "${JOKO_SECURITY_SRC}" && pwd)"
    build_install "${JOKO_SECURITY_SRC}"
fi

ENV_FILE="${REPO_ROOT}/.env"
if [ ! -f "$ENV_FILE" ]; then
    say "Creando .env a partir de env.sample..."
    cp "${REPO_ROOT}/env.sample" "$ENV_FILE"
fi
sed "${SED_IN[@]}" "s#^APPLICATION_ROOT_FOLDER=.*#APPLICATION_ROOT_FOLDER=${REPO_ROOT}#" "$ENV_FILE"
sed "${SED_IN[@]}" "s#^MAVEN_SETTINGS_FOLDER=.*#MAVEN_SETTINGS_FOLDER=${M2_DIR}#" "$ENV_FILE"
say ".env listo: MAVEN_SETTINGS_FOLDER=${M2_DIR}"

say "Verificando la compilación del starter kit..."
( cd "$REPO_ROOT" && mvn -B -q compile -Ddependency-check.skip=true )

cat <<'EOF'

================ RESUMEN ================
¡Todo listo! El backend se puede ejecutar:

  Opción A (Docker):
      docker compose up

  Opción B (Maven):
      mvn spring-boot:run

  Swagger:   http://localhost:8080/swagger-ui.html
  Usuario:   admin / 123456
=========================================
EOF
