# PostgreSQL

El esquema lo aplica **Flyway** al arrancar la aplicación
(`src/main/resources/db/migration`). No hace falta Liquibase.

## 1. Crear la base

```sql
CREATE DATABASE starter_kit;
```

## 2. Configurar el datasource

Copiá `src/main/resources/application.properties.example` a un archivo de
perfil (por ejemplo `/opt/starter-kit/dev/application.properties`) y ajustá
URL, usuario y password.

Propiedades mínimas:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/starter_kit
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=none
joko.secret.mode=BD
joko.security.storage.type=postgres
joko.security.web.enabled=true
```

## 3. Arrancar

```shell
export SPRING_CONFIG_LOCATION=/opt/starter-kit/dev/application.properties
mvn spring-boot:run
```

Flyway crea:

* schema `joko_security` (perfiles, keychain, tokens, sesiones)
* schemas `basic` y `profile` (países, usuarios, notificaciones)
* datos demo (`admin` / `123456`)

## 4. Recrear desde cero

Borrá y volvé a crear la base, o eliminá las tablas de Flyway
(`flyway_schema_history`) y los schemas `joko_security`, `basic` y `profile`.
El próximo arranque vuelve a migrar.

`./scripts/updater fresh` sigue disponible para dropear/crear la base en
flujos PostgreSQL que ya usaban `ENV_VARS`; las migraciones las aplica la
aplicación, no el plugin de Liquibase.
