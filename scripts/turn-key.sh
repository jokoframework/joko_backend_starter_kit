#!/usr/bin/env bash
#
# turn-key.sh — Deja el joko_backend_starter_kit listo para ejecutar sin
# Personal Access Token (PAT) de GitHub.
#
# Hace el bootstrap completo en la máquina:
#   1. SDKMAN + Java 11 + Maven (si no están instalados)
#   2. Clona y compila joko-utils (v0.6.8) y joko-security (v1.2.16) desde
#      los repositorios públicos y los instala en el repositorio local de Maven
#   3. Corrige una descarga problemática conocida (xml-apis-ext vía jitpack)
#   4. Deja configurado .env para docker compose
#   5. Verifica que el proyecto compila
#
# Uso:   ./scripts/turn-key.sh
# Variables de entorno (opcionales):
#   JOKO_SRC_DIR   dónde clonar los repositorios joko (default: ~/git/jokoframework)

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
M2_DIR="${HOME}/.m2"
JOKO_SRC_DIR="${JOKO_SRC_DIR:-${HOME}/git/jokoframework}"
JOKO_UTILS_TAG="v0.6.8"
JOKO_SECURITY_TAG="v1.2.16"
XML_API_EXT_VERSION="1.3.04"

say()  { printf '\n\033[1;36m==> %s\033[0m\n' "$*"; }
warn() { printf '\033[1;33m[!] %s\033[0m\n' "$*"; }

# sed compatible con Linux (GNU) y macOS (BSD)
if sed --version >/dev/null 2>&1; then SED_IN=(-i); else SED_IN=(-i ''); fi

# ---------------------------------------------------------------------------
# 1. SDKMAN
# ---------------------------------------------------------------------------
if [ ! -d "${HOME}/.sdkman" ]; then
    say "Instalando SDKMAN..."
    command -v curl >/dev/null 2>&1 || { echo "ERROR: 'curl' es obligatorio." >&2; exit 1; }
    curl -s "https://get.sdkman.io" | bash
fi

# shellcheck disable=SC1091
set +u
source "${HOME}/.sdkman/bin/sdkman-init.sh"
set -u

# Evita las preguntas interactivas de SDKMAN
SDKMAN_CONFIG="${HOME}/.sdkman/etc/config"
if ! grep -q '^sdkman_auto_answer=true' "$SDKMAN_CONFIG" 2>/dev/null; then
    printf '\nsdkman_auto_answer=true\n' >> "$SDKMAN_CONFIG"
fi

# 'sdk' es una función bash: se la invoca con nounset desactivado (internamente
# referencia variables opcionales como PAGER que disparan 'set -u').
sdk_call() {
    ( set +u; sdk "$@" )
}

# ---------------------------------------------------------------------------
# 2. Java 11
# ---------------------------------------------------------------------------
JAVA_CANDIDATE="$(sdk_call list java 2>/dev/null \
    | perl -pe 's/\e\[[0-9;]*m//g' \
    | grep -oE '11\.0\.[0-9]+-tem' | head -n1 || true)"

if [ -z "${JAVA_CANDIDATE}" ]; then
    warn "No se encontró un Java 11 (Temurin) disponible en SDKMAN. Revisá 'sdk list java'."
    exit 1
fi

if [ ! -d "${HOME}/.sdkman/candidates/java/${JAVA_CANDIDATE}" ]; then
    say "Instalando Java ${JAVA_CANDIDATE} (SDKMAN)..."
    sdk_call install java "$JAVA_CANDIDATE"
else
    say "Java ${JAVA_CANDIDATE} ya está instalado."
fi
export JAVA_HOME="${HOME}/.sdkman/candidates/java/${JAVA_CANDIDATE}"
sdk_call default java "$JAVA_CANDIDATE" >/dev/null 2>&1 || true

# ---------------------------------------------------------------------------
# 3. Maven
# ---------------------------------------------------------------------------
MAVEN_CANDIDATE="$(sdk_call list maven 2>/dev/null \
    | perl -pe 's/\e\[[0-9;]*m//g' \
    | grep -oE '3\.9\.[0-9]+' | sort -V -r | head -n1 || true)"
MAVEN_CANDIDATE="${MAVEN_CANDIDATE:-3.9.16}"

if [ ! -d "${HOME}/.sdkman/candidates/maven/${MAVEN_CANDIDATE}" ]; then
    say "Instalando Maven ${MAVEN_CANDIDATE} (SDKMAN)..."
    sdk_call install maven "$MAVEN_CANDIDATE"
else
    say "Maven ${MAVEN_CANDIDATE} ya está instalado."
fi
export PATH="${HOME}/.sdkman/candidates/maven/${MAVEN_CANDIDATE}/bin:$PATH"
sdk_call default maven "$MAVEN_CANDIDATE" >/dev/null 2>&1 || true

"$JAVA_HOME/bin/java" -version 2>&1 | head -n1
mvn -v 2>/dev/null | head -n1 || true

# ---------------------------------------------------------------------------
# 4. Repositorios públicos de joko (sin PAT)
# ---------------------------------------------------------------------------
mkdir -p "$JOKO_SRC_DIR"

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
    echo "  -> $(basename "$dir") en $(git -C "$dir" describe --tags)"
}

clone_and_checkout "https://github.com/jokoframework/joko-utils.git" \
    "${JOKO_SRC_DIR}/joko-utils" "$JOKO_UTILS_TAG"
clone_and_checkout "https://github.com/jokoframework/security.git" \
    "${JOKO_SRC_DIR}/security" "$JOKO_SECURITY_TAG"

# ---------------------------------------------------------------------------
# 5. Fix xml-apis-ext (el resolver mezcla spring-releases/jitpack y deja el
#    artefacto vacío; se lo baja de Maven Central)
# ---------------------------------------------------------------------------
XML_DIR="${M2_DIR}/repository/xml-apis/xml-apis-ext/${XML_API_EXT_VERSION}"
XML_JAR="${XML_DIR}/xml-apis-ext-${XML_API_EXT_VERSION}.jar"
if [ ! -s "$XML_JAR" ]; then
    say "Bajando xml-apis-ext ${XML_API_EXT_VERSION} desde Maven Central..."
    mkdir -p "$XML_DIR"
    for f in "xml-apis-ext-${XML_API_EXT_VERSION}.jar" \
             "xml-apis-ext-${XML_API_EXT_VERSION}.pom" \
             "xml-apis-ext-${XML_API_EXT_VERSION}.jar.sha1" \
             "xml-apis-ext-${XML_API_EXT_VERSION}.pom.sha1"; do
        curl -sf -o "${XML_DIR}/${f}" \
            "https://repo1.maven.org/maven2/xml-apis/xml-apis-ext/${XML_API_EXT_VERSION}/${f}" \
            || warn "No se pudo descargar ${f}"
    done
else
    say "xml-apis-ext ya está presente en el repositorio local."
fi

# ---------------------------------------------------------------------------
# 6. Compilar e instalar las dependencias joko en el repositorio local
# ---------------------------------------------------------------------------
build_install() {
    local project_dir="$1"
    say "Compilando e instalando $(basename "$project_dir") en ~/.m2 ..."
    ( cd "$project_dir" && mvn -B -q install -DskipTests -Ddependency-check.skip=true )
}

build_install "${JOKO_SRC_DIR}/joko-utils"
build_install "${JOKO_SRC_DIR}/security"

# ---------------------------------------------------------------------------
# 7. Configurar .env para docker compose
# ---------------------------------------------------------------------------
ENV_FILE="${REPO_ROOT}/.env"
if [ ! -f "$ENV_FILE" ]; then
    say "Creando .env a partir de env.sample..."
    cp "${REPO_ROOT}/env.sample" "$ENV_FILE"
fi
sed "${SED_IN[@]}" "s#^APPLICATION_ROOT_FOLDER=.*#APPLICATION_ROOT_FOLDER=${REPO_ROOT}#" "$ENV_FILE"
sed "${SED_IN[@]}" "s#^MAVEN_SETTINGS_FOLDER=.*#MAVEN_SETTINGS_FOLDER=${M2_DIR}#" "$ENV_FILE"
say ".env listo: MAVEN_SETTINGS_FOLDER=${M2_DIR}"

# ---------------------------------------------------------------------------
# 8. Verificar que el starter kit compila
# ---------------------------------------------------------------------------
say "Verificando la compilación del starter kit..."
( cd "$REPO_ROOT" && mvn -B -q compile )

cat <<'EOF'

================ RESUMEN ================
¡Todo listo! El backend se puede ejecutar:

  Opción A (Docker):
      docker compose up

  Opción B (Maven):
      mvn spring-boot:run

  Swagger:   http://localhost:8080/swagger-ui/
  Usuario:   admin / 123456
=========================================
EOF
