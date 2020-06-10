Para ejecutar el Joko Backend, se debe crear la base de datos en el servidor PostgreSQL
Para eso, el proyecto incluye las herramientas basadas en [Liquibase](https://www.liquibase.org/)

El liquibase es un programa hecho en Java, que puede ser ejecutado desde la línea de comando, o como un plugin de maven. 
Describimos a continuación las instrucciones para ejecutarlas desde unos scripts proveidos por el proyecto.

  ### Step 1) Configuración del archivo "development.vars"
  
  Se debe configurar el archivo "development.vars", que servirá para la 
  ejecucion de liquibase. Este es un archivo bash que debe tener dos variables: 
  
  - MVN_SETTINGS: Archivo de configuracion de perfil Maven. En caso de utilizar
   el Artifactory interno, sería el recien descargado. Ej. ` $HOME/.m2/settings.xml` 
   puede utilizar de ejemplo el archivo que se encuentra en 
   `src/main/resources/settings.xml.example` copiando a `$HOME/.m2/settings.xml`
  - `PROFILE_DIR`: Directorio de perfil creado en el punto inicial. Ej. 
  `/opt/starter-kit`
  
  Un ejemplo de este archivo se encuenta en `src/main/resources/development.vars`.
  
  Se recomienda que este archivo esté fuera del workspsace en el directorio 
  padre de los PROFILE_DIR. Ejemplo: ``/opt/starter-kit/``.
  
  ### Step 2) Configuración de variables de entorno
  Exportar variable, desde la terminal:
  ```shell
    $ export ENV_VARS="/opt/starter-kit/development.vars"
  ```
  Obs.: El truco es tener varios archivos `profile.vars` y cada uno apuntando a
   un `PROFILE_DIR` diferente. 
   

  ### Step3 ) Ejecutar Liquibase.
  
1.- Crea la schema de cero.
  ```shell
    $ ./scripts/updater fresh
  ```
2.- Inicializa datos básicos (o reinicializa)
  ```shell
    $ ./scripts/updater seed src/main/resources/db/sql/seed-data.sql
    $ ./scripts/updater seed src/main/resources/db/sql/seed-config.sql
  ```
  **OJO**:
* El parámetro `fresh` elimina la base de datos que está configurada en el `application.properties` y la vuelve a crear desde cero con la última versión del schema
  
* Los parámetros `seed <file>` cargan datos indicados en el archivo `<file>`, para los casos en que se ejecute `fresh` siempre debe ir seguido de un `seed` con el archivo que (re)inicializa los datos básicos del sistema 
  
* Los datos básicos del sistema estan en dos archivos:

`seed-data.sql`: Todos la configuracion base que es independiente al ambiente
`[ambiente]-config`. Por ejemplo: `dev-config.sql` . Posee los parametros de configuracion adecuados  para el ambiente de desarrollo. Tambien existe `qa-config` y `prod-config`
  
3.- Liquibase permite tener una evulución del modelo siguiendo las reglas para su changelog, y para correr el liquibase en modo de actualización ejecute:  
```
    $ ./scripts/updater update
  ```

Más detalles en la documentación del Liquibase.
