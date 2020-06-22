# joko-backend-starter-kit
Este es un modulo que contiene lo que normalmente utilizamos en un proyecto 
backend:
* Integracion con joko-security 1.0.0
* Integracion con swagger 2 (/swagger-ui.html)
* RestController
* Servicios basicos:
    * Users
    * Countries
* Repository + Entity (JPA)
* Integracion con liquibase

La intencion del proyecto es que sirva como un template para crear nuevos 
proyectos.

OBS. Las instrucciones estan orientadas a sistemas UNIX, no obstante, pueden ser adaptadas para otros sistemas.

# Como utilizar el proyecto
## Clonar el proyecto de
https://github.com/jokoframework/joko_backend_starter_kit

## Eliminar la dependencia al repo actual
```
rm -rf .git
git init
``` 

## Ejecutarlo
Para una guia de como correr el proyecto visite [RUN.md](RUN.md)

## Personalizarlo
* Buscar los FIXME y empezar a personalizar
* Eliminar todo lo que quiera

## PUSH al nuevo repo
```
git remote add origin <nuevoURL>
git push origin master
```


