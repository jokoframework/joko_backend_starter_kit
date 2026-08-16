---
name: joko-probar-db
description: >
  Probar el joko_backend_starter_kit contra H2 en modo PostgreSQL o contra un
  PostgreSQL en red externa. Usá cuando el usuario pida smoke, curl, login JWT,
  validar Flyway, "probar contra H2", "probar contra postgres", perfil
  postgres, 192.168.10.24, interfisa_db, o /joko-probar-db. No aplica a
  cambios de negocio ni a mutaciones git.
---

# Probar datasources (H2 modo PG / PostgreSQL externo)

Respondé en español. No mutés git.

## Elegir destino

| Pedido | Destino | Cómo arrancar |
|---|---|---|
| default, "H2", "local", "demo" | H2 file, `MODE=PostgreSQL` | `mvn spring-boot:run` (perfil default) |
| "postgres", "externo", host/puerto/db | PostgreSQL de red | `SPRING_PROFILES_ACTIVE=postgres` + env |

No mezcles los dos en el mismo proceso. Si 8080 está ocupado, cortá **solo** el `spring-boot`/`java` de este repo.

## Contratos que no cambian

- Header: `X-JOKO-AUTH` (nunca `Authorization: Bearer`).
- Demo: `admin` / `123456`.
- Flujo: `POST /api/login` → `POST /api/token/user-access` → `GET /api/secure/users/admin`.
- Público: `GET /api/countries`.
- Swagger: `/swagger-ui.html` (302) y `/v3/api-docs`.
- Schema: Flyway `src/main/resources/db/migration/` (`V1`–`V5`). No uses Liquibase.

Fuente de propiedades: `src/main/resources/application.properties` (H2) y
`src/main/resources/application-postgres.properties` (externo). No dupliques
URL/credenciales en este skill: leé esos archivos.

## 1) H2 modo PostgreSQL

JDBC real (ya está en `application.properties`):

`jdbc:h2:file:~/.joko-starter-kit-db;AUTO_SERVER=true;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH`

`sa` / `123456`. Consola: `/h2-console`.

```bash
mvn -B spring-boot:run -Ddependency-check.skip=true
```

Esperá `Started Application` y `jdbc:h2:file:`. Luego smoke.

Un archivo H2 viejo (pre-2.x / otra Flyway) rompe el arranque: mové
`~/.joko-starter-kit-db*` y reintentá.

## 2) PostgreSQL en red externa

Leé defaults de `application-postgres.properties`. Override con env, no
editando el archivo salvo que el usuario pida otro default permanente.

| Env | Default actual del perfil |
|---|---|
| `DB_EXPOSED_PORT` | `5434` (el `5433` de varios compose **no** es este servicio) |
| `POSTGRES_DB` | `interfisa_db` |
| `POSTGRES_USER` | `app` |
| `POSTGRES_PASSWORD` | `secret` |
| host | `192.168.10.24` |

Antes de arrancar:

1. Probe TCP al host:puerto. Si falla desde un contenedor, probá el gateway
   Docker (`172.18.0.1`) con el **mismo puerto**. No asumas `5432`/`5433`.
2. `pg_isready` + `psql` (`\conninfo`, `\dn`). Sin conexión, **no** arranques Spring.
3. Avisá que Flyway va a crear `joko_security`, `basic`, `profile` y
   `public.flyway_schema_history` en esa base. Si ya hay historia Flyway de
   **otro** proyecto, parar y preguntar. Si la base está vacía de esos schemas,
   seguir.

```bash
export SPRING_PROFILES_ACTIVE=postgres
export DB_EXPOSED_PORT="${DB_EXPOSED_PORT:-5434}"
export POSTGRES_DB="${POSTGRES_DB:-interfisa_db}"
export POSTGRES_USER="${POSTGRES_USER:-app}"
export POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-secret}"

mvn -B spring-boot:run -Ddependency-check.skip=true
```

Log esperado: `The following 1 profile is active: "postgres"`,
`jdbc:postgresql://…`, `PgConnection`, Flyway `v5`, `Started Application`.

Si Hibernate se queja de `H2Dialect` contra Postgres, el perfil no cargó.

## 3) Smoke (obligatorio en ambos destinos)

Con la app en 8080:

```bash
./scripts/smoke-api.sh
```

`BASE_URL` override si el puerto no es 8080. El script falla si countries,
login, access, user protegido o 401 sin token no cierran.

Verificación extra en PostgreSQL (opcional):

```sql
SELECT id, username, profile FROM profile."USER";
SELECT id, name, key FROM joko_security.security_profile;
```

## 4) Tests Maven

`mvn -B test -Ddependency-check.skip=true` usa **siempre**
`src/test/resources/application.properties` → H2 **mem** (`startertest`).
No redirijas Surefire al PostgreSQL externo (base compartida + Flyway).

Eso no sustituye el smoke curl del destino que pidió el usuario.

## Gotchas

- Compilar/OWASP: `-Ddependency-check.skip=true`. Si falta
  `joko-security-starter:2.0.0-SNAPSHOT`, corré `./scripts/turn-key.sh`
  (skill `joko-devops` está desactualizado: habla de Java 11 / security 1.2.16).
- Path Security 6: no uses `/**/heartbeat` (rompe el matcher). Heartbeats
  explícitos en `AuthorizationManagerImpl`.
- JWT 2.x firma HS384; el body de login/access trae el token en `secret`.
- Desde sandbox Docker, `192.168.10.24` a veces sí responde y el localhost del
  contenedor no publica 5434. Probe, no adivines.

## Informe al usuario

Decí destino (H2 file vs host:puerto/db), si Flyway migró o ya estaba en v5,
tabla de HTTP del smoke, y si `mvn test` se corrió (H2 mem, aparte).
