# AGENTS.md

Spring Boot 3.5.16 / Java 21 / Maven backend starter kit (Joko framework). Docs and comments are mostly in Spanish; code is `io.github.jokoframework.myproject`. It is a **template** — new projects fork it and rename the package.

## Build prerequisites

- Maven wrapper is available (`./mvnw`). `scripts/turn-key.sh` bootstraps SDKMAN + Java 21 + Maven if the runtime is older than 21.
- `joko-security` 2.x and `joko-utils` are **not on Maven Central**. `scripts/turn-key.sh` reads versions from this `pom.xml` and **skips clone/install if those artifacts are already in `~/.m2`**. Otherwise it builds `joko-utils` from the public tag `v{joko-utils.version}` and `joko-security` from the sibling `../security` (`JOKO_SECURITY_SRC` override). No GitHub PAT. Tests + OWASP are skipped during that install.
- `mvn verify` / `package` runs OWASP Dependency-Check 13 in **warning-only** mode (`failBuildOnCVSS=11`). It needs `NVD_API_KEY` for a useful NVD update. Skip with `-Ddependency-check.skip=true`. Strict gate: `-Ddependency-check.failBuildOnCVSS=8`. Report: `target/dependency-check-report.html`.

## Run

- Docker: after `./scripts/turn-key.sh` (which writes `.env`), run `docker compose up`. The compose mounts the host `.m2` dir as `/root/.m2` and maps debug port 5005.
- Maven: `mvn spring-boot:run` — uses file H2 (`~/.joko-starter-kit-db`, `sa`/`123456`). Default API login: `admin`/`123456`. Swagger at `/swagger-ui.html` (`/` redirects there). Jar: `target/joko-backend-starter-kit-2.0.0.jar`.
- Grab a token for authenticated endpoints: `scripts/token-localhost.sh` (logs in via `/api/login` → `/api/token/user-access`, prints access token).

## Agent routing

- **Smoke against H2 (PostgreSQL mode) or an external PostgreSQL** (curl JWT flow, Flyway, profile `postgres`, host/port/db) → load `.opencode/skills/joko-probar-db/SKILL.md` and follow it. Do not invent datasource URLs; read `application.properties` / `application-postgres.properties`.
- **Never mutate git state**: no commits, no `git config`, `clone` of this repo, `init`, `reset`, force operations, etc. Only read-only git is allowed (`status`, `diff`, `log`). The user runs anything that writes to `.git`.
- Respond to the user in Spanish; keep AGENTS.md and agent-facing notes in English.

## Code conventions

- Entrypoint `Application.java` scans only `io.github.jokoframework.myproject`. joko-security 2.x beans come from Spring Boot auto-configuration (`joko-security-autoconfigure`).
- Controllers extend `BaseRestController` where useful. **Routes are string constants in `constants/ApiPaths.java`** — add new paths there, not inline.
- Layers per feature: `web/controller` → `{profile, basic}/service` (interface + `service/impl`) and `profile/manager` → `repositories` → JPA entities. Entity↔DTO mapping via `joko-utils` `BaseEntity` / `DTOUtils`.
- Auth comes from `joko-security-starter`: login/token endpoints (`/api/login`, `/api/token/user-access`), protected routes under `/api/secure/**`. Custom auth logic lives in `auth/AuthenticationManagerImpl` (`JokoAuthenticationManager`). Set `joko.security.web.enabled=true`.
- Header is `X-JOKO-AUTH`. Do not switch to `Authorization: Bearer`.

## DB / Flyway

- Schema is Flyway-managed under `src/main/resources/db/migration/`: `V1`–`V3` joko-security, `V4` application schemas (`basic`, `profile`), `V5` demo seeds. **New changes go in a new `V{n}__…sql` file.** Do not recreate joko-security tables.
- H2 is the default demo store. PostgreSQL: point `spring.datasource.*` at the server; Flyway runs on startup. See `PostgreSQL.md`.
- Profile overrides: `application.properties` can be replaced via `SPRING_CONFIG_LOCATION`.

## Tests

- `src/test` has a context-load test and an auth-flow integration test (H2 in-memory + Flyway).
