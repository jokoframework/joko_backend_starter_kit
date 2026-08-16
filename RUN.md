# Guía de ejecución

## Requisitos

* Sistema UNIX (Linux/macOS) con `bash`, `git`, `curl`.
* No hace falta tener Java ni Maven instalados: el script de puesta a punto
  los instala vía SDKMAN si faltan (Java 21+).
* **No se necesita ningún Personal Access Token (PAT) de GitHub.**
  `joko-utils` se clona del repositorio público. **joko-security 2.x** se
  instala desde el parent modular local (`JOKO_SECURITY_SRC`).

## 1) Puesta a punto automática (turn-key)

Desde la raíz del proyecto:

```shell
./scripts/turn-key.sh
```

El script es idempotente y hace lo siguiente:

1. Instala SDKMAN si falta, y con él Java 21 y Maven 3.9.x si el Java del
   sistema es menor a 21.
2. Lee `joko-utils.version` y `joko-security.version` del `pom.xml`.
   Si esos artefactos **ya están en `~/.m2`**, no clona ni compila.
3. Si faltan: clona `joko-utils` (tag `v{versión}`) y/o instala el parent
   `joko-security` 2.x desde el hermano `../security` (`JOKO_SECURITY_SRC`).
4. Deja configurado `.env` para Docker.
5. Verifica que el proyecto compila.

Variables de entorno opcionales:

| Variable | Uso | Default |
|---|---|---|
| `JOKO_SRC_DIR` | Dónde clonar `joko-utils` | `~/git/jokoframework` |
| `JOKO_SECURITY_SRC` | Parent modular 2.x | `../security` (hermano del kit) |
| `JOKO_REMOTE_ROOT` | Raíz del proyecto en el remoto (rsync) | (sin default) |
| `SSH_TARGET` | Target ssh `usuario@host` | (sin default) |

```shell
JOKO_SECURITY_SRC=/ruta/al/security ./scripts/turn-key.sh
```

Las variables de shell **no van en `.env`**: ese archivo es solo para
`docker compose` (`MAVEN_SETTINGS_FOLDER`).

Ejemplo de sync local → remoto:

```shell
export JOKO_SRC_DIR="$HOME/git/jokoframework"
export JOKO_REMOTE_ROOT="/git/jokoframework"
export SSH_TARGET="usuario@host"

rsync -avz --exclude='.git/' --exclude='target/' --exclude='.idea/' \
  --exclude='.vscode/' --exclude='.env' \
  "${JOKO_SRC_DIR}/joko_backend_starter_kit/" \
  "${SSH_TARGET}:${JOKO_REMOTE_ROOT}/joko_backend_starter_kit/"
```

## 2) Ejecutar el backend

### Opción 1: Docker

```shell
docker compose up
```

Levanta el servicio en http://localhost:8080 (debug remoto en el puerto 5005).
El `docker-compose.yml` monta tu `~/.m2` como `/root/.m2` dentro del contenedor.

### Opción 2: Maven

```shell
mvn spring-boot:run
# o
./mvnw spring-boot:run
```

Usa H2 en `~/.joko-starter-kit-db`. Flyway crea el esquema al arrancar.

- Swagger: http://localhost:8080/swagger-ui.html
- Usuario: `admin` / `123456`
- Token: `scripts/token-localhost.sh`

## 3) Autenticación

```http
POST /api/login
Content-Type: application/json

{"username":"admin","password":"123456"}
```

La respuesta trae el refresh token en `secret`. Luego:

```http
POST /api/token/user-access
X-JOKO-AUTH: {refresh_token}
```

Las rutas protegidas usan el access token en el mismo header `X-JOKO-AUTH`.
No se usa `Authorization: Bearer`.

## 4) Smoke test

Comprueba el contrato JWT de dos pasos: público sin token, login → refresh,
access solo con refresh, API de negocio solo con access, logout solo con
refresh (revoca ese refresh; el access ya emitido sigue hasta `exp`).

Demo: `admin` / `123456`. Header: `X-JOKO-AUTH` (JWT crudo, sin `Bearer`).

```shell
export BASE_URL="${BASE_URL:-http://localhost:8080}"
# remoto: export BASE_URL=http://192.168.10.24:8080
```

Atajo de los primeros pasos (sin logout): `./scripts/smoke-api.sh`
(`BASE_URL` también aplica).

### Resultados esperados

| Paso | Llamada | HTTP |
|---|---|---|
| 1 | `GET /api/countries` sin token | 200 |
| 1b | `GET /api/secure/users/admin` sin token | 401 |
| 2 | `POST /api/login` | 200 (`secret` = refresh) |
| 3 | `POST /api/token/user-access` + refresh | 200 (`secret` = access) |
| 4 | `GET /api/secure/users/admin` + access | 200 |
| 4b | misma ruta + refresh | 403 |
| 5 | `POST /api/logout` + access | 403 |
| 5b | `POST /api/logout` + refresh | 202 |
| 5c | `user-access` otra vez con ese refresh | 401 |
| 5d | protegida con el access de antes | 200 (hasta que expire) |

### Curls

```shell
# 1) Público, sin autenticación
curl -sS -w '\nHTTP %{http_code}\n' "$BASE_URL/api/countries"

# 1b) Protegido, sin token
curl -sS -w '\nHTTP %{http_code}\n' "$BASE_URL/api/secure/users/admin"

# 2) Login → refresh token
curl -sS -w '\nHTTP %{http_code}\n' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}' \
  "$BASE_URL/api/login" | tee /tmp/joko-login.json

REFRESH=$(python3 -c "import json; print(json.load(open('/tmp/joko-login.json')).get('secret') or '')")

# 3) Access token (exige refresh)
curl -sS -w '\nHTTP %{http_code}\n' \
  -X POST -H "X-JOKO-AUTH: $REFRESH" \
  "$BASE_URL/api/token/user-access" | tee /tmp/joko-access.json

ACCESS=$(python3 -c "import json; print(json.load(open('/tmp/joko-access.json')).get('secret') or '')")

# 4) Consulta protegida con access
curl -sS -w '\nHTTP %{http_code}\n' \
  -H "X-JOKO-AUTH: $ACCESS" \
  "$BASE_URL/api/secure/users/admin"

# 4b) La misma ruta con refresh (403: tipo de token incorrecto)
curl -sS -w '\nHTTP %{http_code}\n' \
  -H "X-JOKO-AUTH: $REFRESH" \
  "$BASE_URL/api/secure/users/admin"

# 5) Logout con access (403: logout pide autoridad Refresh)
curl -sS -w '\nHTTP %{http_code}\n' \
  -X POST -H "X-JOKO-AUTH: $ACCESS" \
  "$BASE_URL/api/logout"

# 5b) Logout con refresh (202: revoca ese JTI)
curl -sS -w '\nHTTP %{http_code}\n' \
  -X POST -H "X-JOKO-AUTH: $REFRESH" \
  "$BASE_URL/api/logout"

# 5c) Ese refresh ya no emite access
curl -sS -w '\nHTTP %{http_code}\n' \
  -X POST -H "X-JOKO-AUTH: $REFRESH" \
  "$BASE_URL/api/token/user-access"

# 5d) El access emitido antes del logout sigue válido hasta exp
curl -sS -w '\nHTTP %{http_code}\n' \
  -H "X-JOKO-AUTH: $ACCESS" \
  "$BASE_URL/api/secure/users/admin"
```

Otros públicos útiles: `GET /api/notifications/types`,
`GET /api/secure/users/heartbeat`, `GET /v3/api-docs`,
`GET /swagger-ui.html` (302 → `/swagger-ui/index.html`).
