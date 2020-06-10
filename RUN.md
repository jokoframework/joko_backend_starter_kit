## Requisitos para ejecutar el servicio

* IDE compatible con proyectos MAVEN
* Java 11 (JDK11)
* Por default se usa la base de datos h2. Si desea usar PostgreSQL, lea [PostgreSQL.md](PosgreSQL.md)


## Clonar proyecto

Debe clonar el proyecto de (Es un repositorio autenticado, consultar el URL para `clone` en):

```shell
$ git clone XXXXXX
$ cd XXXX
```

## Configuración
El proyecto posee un conjunto de scripts que nos permiten automatizar el ciclo
 de vida de la base de datos. Con esto se puede crear facilmente toda la BD 
 desde la linea de comandos. Para actualizar hay que seguir los siguientes 
 pasos:
  
 ### Step 1) Crear el directorio PROFILE_DIR
 El directorio de profile contiene el archivo application.properties con la 
 configuracion necesaria para lanzar la aplicacion spring-boot.
Esto permite tener diferenciados los ambientes de ejecución.

 La convencion utilizada es tener un directorio, dentro del cual existan 
 varios PROFILE_DIR segun se requiera. Por ejemplo:
 ```shell
 /opt/starter-kit/dev
 /opt/starter-kit/qa
 ```
 
 En el anterior ejemplo existen dos PROFILE_DIR dentro del joko-demo, el 
 primero para development y el segundo con datos de quality assurance.
 
 Para referirse al PROFILE_DIR como parametro se usa "ext.prop.dir".
 
 Obs.: Un archivo de ejemplo para el application.properties se encuentra en 
 `src/main/resources/application.properties.examples`
  
  ### Step 2) Instalar librerias Joko
	
Clonar los proyectos (no dentro de la misma carpeta backend o PROFILE)
	
https://github.com/jokoframework/joko-utils

https://github.com/jokoframework/security

Entrar en el directorio de jada proyecto y hacer lo siguiente:

-Para Joko-utils solo ejecutar `mvn install`, en caso de tener problemas descargando las dependencias ejecute
 
 `mvn install -Ddownloader.quick.query.timestamp=false`

-Para [security](https://github.com/jokoframework/security) hay que entrar al proyecto en github y seguir sus instrucciones de instalacion. Esto deja instaladas las librerías que son dependencias para el Backend.

### Step 3 Corren con Maven

Una vez hechos estos cambios, debemos definir y exportar la siguiente variable de entorno:

```shell
  $ export SPRING_CONFIG_LOCATION=/opt/starter-kit/dev/application.properties
```

Posteriormente, podemos correr el proyecto como una aplicación de Spring Boot, o con la siguiente línea de comando (se requiere maven instalado).

```shell
  $ mvn -Dext.prop.dir=/opt/starter-kit/dev spring-boot:run
```


El usuario/password default que se crea con la base de datos, es admin/123456

OBS. Se recomienda este metodo, ya que con STS habria que hacer ciertas configuraciones extras para ejecutar desde ahi.
Queda a eleccion del lector.

A partir de este punto, cada vez que se decida levantar y bajar el servidor del Backend, se deben ejecutar los siguientes comandos en la terminal desde la carpeta del proyecto joko backend starter-kit:

```shell
  $ export ENV_VARS="/opt/starter-kit/development.vars"
  $ ./scripts/updater fresh
  $ ./scripts/updater seed src/main/resources/db/sql/seed-data.sql
  $ ./scripts/updater seed src/main/resources/db/sql/seed-config.sql
  $ export SPRING_CONFIG_LOCATION=/opt/starter-kit/dev/application.properties
  $ mvn -Dext.prop.dir=/opt/starter-kit/dev spring-boot:run
```

STS
----
Para poder levantar la apliación desde un IDE, se debe añadir el parámetro 
como argumento de la VM `-Dspring.config.location=file://` 
 con la ruta al archivo  application.properties. 
 
 Por ejemplo en el IDE  Eclipse-STS  ir hasta debug/debug-configuracion,  añadir una nueva configuración e ir hasta la pestaña (x)Arguments Luego insertar el parámetro en el campo 'VM arguments'. 
 
 Ejemplo:

    -Dspring.config.location=file:///opt/starter-kit/application.properties -Dext
    .prop.dir=/opt/starter-kit


La mayoría de los IDEs soportan ejecución de aplicaciones tipo Spring Boot o 
permiten configurar ejecuciones customizadas de maven.

### Swagger API
El proyecto cuenta con documentación del API accesible desde el swagger-ui.
URI al swagger desde maquina HOST:

  http://localhost:8080/swagger-ui.html
	
OBS. Si se desea abrir la pagina desde algun Windows u otro SO interno:

  http://"IP DE LA MAQUINA HOST":8080/swagger-ui.html
