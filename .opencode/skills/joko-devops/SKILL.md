---
name: joko-devops
description: Usá cuando vayas a ajustar devops/tooling del backend starter kit de Joko: bootstrap con scripts/turn-key.sh, SDKMAN/Java 11/Maven, build local de joko-utils y joko-security, xml-apis-ext, Docker/docker compose, OWASP dependency-check, o rsync entre máquinas (sync de proyectos sin credenciales git de escritura). No aplicar a código de negocio.
---

# Devops del starter kit de Joko

Referencia operativa para tocar tooling/infra de este repo (Spring Boot, puerto 8080,
usuario `admin` / `123456`, Swagger en `/swagger-ui/`, endpoint `/api/countries`).

## Contexto

- Este equipo NO tiene credenciales git de escritura: el flujo de build es **sin PAT**.
  Todo se resuelve con repos públicos y el repositorio local de Maven (`~/.m2`).
- Dependencias de Joko, compiladas e instaladas localmente como jars planos (sin `BOOT-INF`):
  - `io.github.jokoframework:joko-utils:0.6.8` ← repos público `jokoframework/joko-utils`, tag `v0.6.8`
  - `io.github.jokoframework:joko-security:1.2.16` ← repos público `jokoframework/security`, tag `v1.2.16` (ojo: el artifact es `joko-security`)
- Layout de fuentes: `$JOKO_SRC_DIR` (default `~/git/jokoframework`) contiene
  `{joko_backend_starter_kit,joko-utils,security}`.

## Bootstrap: `scripts/turn-key.sh`

Script idempotente que deja el proyecto listo para correr. Prerrequisitos:
`bash`, `git`, `curl`, `unzip`, `perl`.

Qué hace (en orden):

1. SDKMAN (instala si falta).
2. Java 11 (`JAVA_CANDIDATE`, ej. `11.0.32-tem`).
3. Maven 3.9.x (elige la **última** versión 3.9.x disponible).
4. Clona `joko-utils` y `security` en los tags indicados (skip si ya están, con `git -C <dir> fetch --quiet --tags origin` + checkout).
5. Corrige `xml-apis-ext` (ver gotchas).
6. `mvn -B -q install -DskipTests -Ddependency-check.skip=true` en ambos repos.
7. Genera/configura `.env` para Docker (rutas locales).
8. Verifica con `mvn -B -q compile` y muestra el resumen.

Gotchas críticos (ya mordieron dos veces):

- **`set -u` (nounset) rompe SDKMAN**: tanto `source ~/.sdkman/bin/sdkman-init.sh`
  (variable `SDKMAN_CANDIDATES_API` sin definir) como la función bash `sdk`
  (variable `PAGER`). Siempre usar un subshell:
  ```bash
  sdk_call() { ( set +u; sdk "$@"; ) }
  ```
  y hacer `source` con `set +u` alrededor.
- Elegir la última 3.9.x con `sort -V -r | head -n1` — NO el primer match de `sdk list`.
- Limpiar ANSI de la salida de `sdk` con `perl -pe 's/\e\[[0-9;]*m//g'` (portable).
- `sed -i` no es portable: usar `SED_IN=(-i)` para GNU vs `SED_IN=(-i '')` para BSD.
- `xml-apis-ext:1.3.04` llega corrupto desde el repositorio remoto; precargarlo desde
  Maven Central (`repo1.maven.org`) en
  `~/.m2/repository/xml-apis/xml-apis-ext/1.3.04/`.

Verificación:

- Rápida: `mvn -B -q compile` (~1 min).
- NO usar `mvn clean package` para verificar: dispara OWASP dependency-check
  (`failBuildOnCVSS=8` en el pom) y es lento. Para saltarlo:
  `-Ddependency-check.skip=true`.

## Docker

- **NO usar** la base `maven:3.6.3-jdk-11-slim` (Debian buster: repos apt en 404).
  La base correcta es `maven:3.9.6-eclipse-temurin-11` (ver `Dockerfile`).
- `.env` es configuración **por máquina** (gitignored). Campos clave:
  `APPLICATION_ROOT_FOLDER` y `MAVEN_SETTINGS_FOLDER` (el `~/.m2` local se monta como `/root/.m2`
  en el contenedor). `scripts/turn-key.sh` lo regenera con rutas locales.
- Correr: `docker compose up`. App queda en `http://localhost:8080/swagger-ui/`.

## Sincronización entre máquinas (rsync)

Variables de entorno como placeholders (ajustar por máquina; `JOKO_SRC_DIR` es la misma
que usa `scripts/turn-key.sh`):

```bash
export JOKO_SRC_DIR="$HOME/git/jokoframework"   # raíz local de los proyectos
export JOKO_REMOTE_ROOT="/git/jokoframework"    # raíz en el remoto (ajustar al layout real)
export SSH_TARGET="USUARIO@HOST"                # target ssh (usuario@host)
```

Mismo layout relativo en ambos lados: `$JOKO_SRC_DIR/<proyecto>` ↔ `$JOKO_REMOTE_ROOT/<proyecto>`
(el remoto es el que tiene credenciales git de escritura).

- **Local → remoto** (push de cambios):
  ```bash
  rsync -avz --exclude='.git/' --exclude='target/' --exclude='.idea/' --exclude='.vscode/' --exclude='.env' \
    "${JOKO_SRC_DIR}/<proyecto>/" "${SSH_TARGET}:${JOKO_REMOTE_ROOT}/<proyecto>/"
  ```
- **Remoto → local** (pull/mirror): mismo comando con `--delete` (agregar) e invirtiendo source/dest.
- Nota: `JOKO_REMOTE_ROOT` debe ser una ruta absoluta del remoto (un `~` dentro de la variable
  no se expande en el lado remoto al usarla entre comillas).
- Exclusions por qué:
  - `.git/`: preserva la historia git del destino (ahí se commitea/pushea).
  - `target/`: artefactos de build locales; se recompila.
  - `.idea/`, `.vscode/`: metadatos de IDE locales.
  - `.env`: rutas por máquina; `turn-key.sh` lo regenera.
- Siempre probar con `-n` (dry-run) antes de correr con `--delete`.
- `joko-utils` y `security` solo se clonan (no se modifican): no hace falta sincronizarlos si
  no los tocaste; el `turn-key.sh` del destino los clona y compila solo.

## Reglas de git (aplican a los agentes)

- **NUNCA** ejecutar `git commit` ni nada que altere el estado permanente de `.git`
  (`git config`, `git clone`, `git init`, `git reset`, `git checkout -b`, etc.).
  El usuario ejecuta esas operaciones.
- Sí se pueden correr operaciones de solo lectura: `git status`, `git diff`, `git log`.
- Las máquinas pueden no tener identidad git configurada. Si un commit falla con
  `Author identity unknown`, avisar que configuren (local, sin `--global`):
  `git config user.name "..."` / `git config user.email "..."` — no hacerlo vos.
- Al proponer un mensaje de commit, seguir el estilo del repo (español, mensajes
  semánticos tipo `build: ...`, `docs: ...`).

## Cheat-sheet de rutas

- `scripts/turn-key.sh` — bootstrap.
- `~/.sdkman/` — SDKMAN + candidatos (Java/Maven).
- `~/.m2/repository/io/github/jokoframework/{joko-utils/0.6.8,joko-security/1.2.16}` — jars instalados.
- `~/.m2/repository/xml-apis/xml-apis-ext/1.3.04/` — fix manual de Maven Central.
- `$JOKO_SRC_DIR/` — fuentes de los tres proyectos (default `~/git/jokoframework`).
