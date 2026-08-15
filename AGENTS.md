# AGENTS.md

Spring Boot 2.7.16 / Java 11 / Maven backend starter kit (Joko framework). Docs and comments are mostly in Spanish; code is `io.github.jokoframework.myproject`. It is a **template** — new projects fork it and rename the package.

## Build prerequisites

- No Maven wrapper — `mvn` must be on `PATH`. `scripts/turn-key.sh` bootstraps it (SDKMAN + Java 11 + Maven) if missing.
- `joko-security` and `joko-utils` are **not on Maven Central**. They are built locally from their **public** GitHub repos — **no GitHub PAT needed**. Run `scripts/turn-key.sh` once: it clones `jokoframework/joko-utils` (tag `v0.6.8`) and `jokoframework/security` (tag `v1.2.16`) into `$JOKO_SRC_DIR` (default `~/git/jokoframework`), `mvn install`s each (tests + OWASP skipped), and pre-fetches the known-corrupt `xml-apis-ext:1.3.04` artifact from Maven Central.
- `mvn clean package` runs the OWASP dependency-check plugin (bound to `verify`, `failBuildOnCVSS=8`, suppressions in `dependency-check-suppressions.xml`). It is slow and needs network/NVD. Use `mvn compile` for a fast smoke check.

## Run

- Docker (easiest): after `./scripts/turn-key.sh` (which writes `.env`), run `docker compose up`. The compose mounts the host `.m2` dir as `/root/.m2` in the container and maps debug port 5005.
- Maven: `mvn spring-boot:run` — uses embedded H2 DB (`~/.joko-DEMO-DB`, `sa`/`123456`). Default API login: `admin`/`123456`. Swagger at `/swagger-ui/` (`/` redirects there). Jar: `target/joko-backend-starter-kit-1.0.9.jar`.
- Grab a token for authenticated endpoints: `scripts/token-localhost.sh` (logs in via `/api/login` → `/api/token/user-access`, prints access token).

## Code conventions

- Entrypoint `Application.java` `@ComponentScan`/`@EnableJpaRepositories`/`@EntityScan` over all `io.github.jokoframework` packages (this also picks up the `joko-security` lib beans).
- Controllers extend `BaseRestController` (heartbeat/logout/current-user helpers). **Routes are string constants in `constants/ApiPaths.java`** — add new paths there, not inline.
- Layers per feature: `web/controller` → `{profile, basic}/service` (interface + `service/impl`) and `profile/manager` → `repositories` → JPA entities. Entity↔DTO mapping via Orika (`mapper/OrikaBeanMapper`).
- Auth comes from the `joko-security` library, not local config: login/token endpoints (`/api/login`, `/api/token/user-access`), protected routes under `/api/secure/**`. Custom auth logic lives in `auth/AuthenticationManagerImpl` (`JokoAuthenticationManager`).
- `spring.mvc.pathmatch.matching-strategy=ant_path_matcher` is required for Springfox 3 swagger — keep it if touched.

## DB / Liquibase

- Schema is Liquibase-managed: `src/main/resources/db/liquibase/db-changelog.xml` includes `db-changelog-initial.xml`, `db-changelog-joko-security-v1.0.1.xml`, and `db-changelog-evolution.xml`. **New changesets go in the `-evolution` file**, using `schemaName="basic"` or `"profile"` (joko-security tables come from the library changelog — don't recreate).
- PostgreSQL workflow: `scripts/updater` requires `ENV_VARS` pointing to a `development.vars` file exporting `MVN_SETTINGS` and `PROFILE_DIR` (example: `src/main/resources/development.vars`). Commands: `./scripts/updater fresh` (creates schema; the old DB is **renamed, not dropped** — see `scripts/lib/drop-create-db`), `./scripts/updater seed <file>`, `./scripts/updater update`.
- Profile overrides: `application.properties` (H2 default) can be replaced via `-Dext.prop.dir=<dir>` + `-Dspring.config.location=file://<dir>/application.properties` (see `scripts/run.sh`, `scripts/clean.sh`). The `properties-maven-plugin` loads these into Maven props so the liquibase plugin can read datasource settings.

## Known stale things

- No tests exist (no `src/test`). `scripts/test.sh` references machine-specific paths — don't rely on it.
- `scripts/db_fresh.sh` seeds files that don't exist in this repo (only `seed-config.sql` and `seed-data.sql` are present).
