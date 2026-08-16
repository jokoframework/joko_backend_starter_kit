# joko-backend-starter-kit

Plantilla backend para proyectos nuevos del ecosistema Joko:

* Autenticación JWT con **joko-security 2.x** (arquitectura modular)
* Spring Boot 3.5.16 / Java 17
* Documentación OpenAPI (Springdoc) en `/swagger-ui.html`
* Servicios de ejemplo: usuarios, países, notificaciones
* JPA + Flyway
* H2 embebido para demo; PostgreSQL documentado

## Requisitos

* Java 17+
* Maven 3.9+ (o el wrapper `./mvnw`)
* `joko-security` 2.x y `joko-utils` instalados en el repositorio local de Maven

Las dependencias Joko no están en Maven Central. Se instalan en local con `./scripts/turn-key.sh` (sin Personal Access Token).

## Arrancar (turn-key)

```shell
./scripts/turn-key.sh
```

El script instala Java 17 y Maven si faltan, compila `joko-utils` y el parent modular de joko-security 2.x, y verifica que este proyecto compile. Después:

```shell
# Opción 1: Docker
docker compose up

# Opción 2: Maven
mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Usuario demo: `admin` / `123456`
- Header de autenticación: `X-JOKO-AUTH` (no `Authorization: Bearer`)

Flujo: `POST /api/login` → refresh token → `POST /api/token/user-access` → access token → llamadas a `/api/secure/**`. Logout: `POST /api/logout` con el **refresh**.

Smoke test (curls y códigos esperados): [RUN.md](RUN.md#4-smoke-test). PostgreSQL: [PostgreSQL.md](PostgreSQL.md).

## Seguridad de dependencias

[OWASP](https://owasp.org/) (*Open Worldwide Application Security Project*) Dependency-Check **13** corre en `verify`. **Default: solo warning** (`failBuildOnCVSS=11`; no corta el build).

```bash
export NVD_API_KEY='…'   # https://nvd.nist.gov/developers/request-an-api-key

./mvnw clean verify                                 # scan en warning
./mvnw clean verify -Ddependency-check.skip=true    # sin scan
./mvnw clean verify -Ddependency-check.failBuildOnCVSS=8  # falla si CVSS >= 8
```

Informe: `target/dependency-check-report.html`. La consola lista CVE sin score; el CVSS está en el HTML. `./scripts/turn-key.sh` sigue compilando con `-Ddependency-check.skip=true`.

## Personalizarlo (proyecto nuevo)

* `rm -rf .git && git init` para desvincularlo de este repositorio.
* Renombrar el paquete `io.github.jokoframework.myproject`.
* Actualizar `groupId`, `artifactId`, `version` y `start-class` en `pom.xml`.
* Implementar `JokoAuthenticationManager` y `JokoAuthorizationManager` con las reglas del dominio.
* Eliminar los servicios de ejemplo que no se necesiten.
* El escaneo de componentes queda en `io.github.jokoframework.myproject`. Los beans de joko-security los registra el auto-configure.

## De 1.x a 2.x

| 1.x | 2.x |
|---|---|
| `joko-security` 1.2.x (JAR único) | `joko-security-starter` 2.x (core + storage + web + autoconfigure) |
| Spring Boot 2.7 / Java 11 | Spring Boot 3.5.16 / Java 17 |
| Springfox | Springdoc (`/swagger-ui.html`) |
| Liquibase | Flyway (`src/main/resources/db/migration`) |
| `javax.*` | `jakarta.*` |

## PUSH al nuevo repo

```shell
git remote add origin <nuevoURL>
git push origin master
```
