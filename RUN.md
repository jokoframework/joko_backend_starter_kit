# Guía de ejecución

## Requisitos
* Sistema UNIX (Linux/macOS) con `bash`, `git`, `curl`, `unzip` y `perl`.
* No hace falta tener Java ni Maven instalados: el script de puesta a punto
  los instala vía SDKMAN.
* **No se necesita ningún Personal Access Token (PAT) de GitHub.** Las
  dependencias `joko-security` y `joko-utils` se compilan desde los
  repositorios públicos de jokoframework.

## 1) Puesta a punto automática (turn-key)

Desde la raíz del proyecto:

```shell
./scripts/turn-key.sh
```

El script es idempotente (se puede volver a ejecutar sin problema) y hace lo
siguiente:
1. Instala SDKMAN si falta, y con él Java 11 y Maven 3.9.x si faltan.
2. Clona los repositorios públicos `jokoframework/joko-utils` (tag `v0.6.8`)
   y `jokoframework/security` (tag `v1.2.16`).
3. Los compila e instala en el repositorio local de Maven (`~/.m2/repository`).
4. Corrige una descarga problemática conocida de `xml-apis-ext`.
5. Deja configurado `.env` para Docker.
6. Verifica que el proyecto compila.

El directorio donde se clonan los repos se puede cambiar con la variable
`JOKO_SRC_DIR`:

```shell
JOKO_SRC_DIR=/ruta/a/donde/clonar ./scripts/turn-key.sh
```

## 2) Ejecutar el backend

### Opción 1: Docker (recomendada)
```shell
docker compose up
```
Levanta el servicio en http://localhost:8080 (debug remoto en el puerto 5005).
El `docker-compose.yml` monta tu `~/.m2` como `/root/.m2` dentro del
contenedor.

### Opción 2: Maven
```shell
mvn spring-boot:run
```
Usa por defecto una base de datos embebida H2 (`~/.joko-DEMO-DB`, usuario
`sa` / `123456`). Si querés usar PostgreSQL, leé [PostgreSQL.md](PostgreSQL.md).

## Credenciales y endpoints
* Usuario/clave por defecto: `admin` / `123456`
* Swagger UI: http://localhost:8080/swagger-ui/
* Los endpoints autenticados viven bajo `/api/secure/**`. Para obtener un
  token de acceso: `scripts/token-localhost.sh`.

## Empaquetado
```shell
mvn clean package
```
El JAR queda en `target/joko-backend-starter-kit-<version>.jar` (mirá
`<version>` en `pom.xml`).
