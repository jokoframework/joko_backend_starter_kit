# joko-backend-starter-kit
Este es un módulo que contiene lo que normalmente utilizamos en un proyecto
backend:
* Integración con joko-security
* Integración con swagger 2 (/swagger-ui/)
* RestController
* Servicios básicos:
    * Users
    * Countries
* Repository + Entity (JPA)
* Integración con liquibase

La intención del proyecto es que sirva como un template para crear nuevos
proyectos.

OBS. Las instrucciones están orientadas a sistemas UNIX, no obstante, pueden
ser adaptadas para otros sistemas.

# Cómo utilizar el proyecto

## Clonar el proyecto
```shell
git clone https://github.com/jokoframework/joko_backend_starter_kit
cd joko_backend_starter_kit
```

## Arrancar el backend (turn-key)
Las dependencias `joko-security` y `joko-utils` se compilan localmente desde
sus repositorios públicos — **no se necesita ningún token privado (PAT)**.
Un solo comando deja todo listo:

```shell
./scripts/turn-key.sh
```

Ese script instala las herramientas necesarias (SDKMAN, Java 11 y Maven si
faltan), clona y compila las dependencias joko, y verifica que el proyecto
compila. Después de eso:

```shell
# Opción 1: Docker
docker compose up

# Opción 2: Maven
mvn spring-boot:run
```

Para una guía más detallada visite [RUN.md](RUN.md).

## Personalizarlo (para crear un proyecto nuevo)
* `rm -rf .git && git init` para desvincularlo de este repositorio.
* Renombrar el paquete `io.github.jokoframework.myproject` al del nuevo proyecto.
* Actualizar `groupId`, `artifactId`, `version` y `start-class` en `pom.xml`.
* Eliminar los servicios de ejemplo que no se necesiten.

## PUSH al nuevo repo
```shell
git remote add origin <nuevoURL>
git push origin master
```
