## Requisitos para ejecutar el servicio
* IDE compatible con proyectos MAVEN o tener instalado MAVEN en su equipo
* Java 11 (JDK11)
* Por default se usa la base de datos embedded h2. Si desea usar PostgreSQL, lea [PostgreSQL.md](PostgreSQL.md)


## Clonar proyecto
Debe clonar el proyecto de https://github.com/jokoframework/joko_backend_starter_kit.git

```shell
$ git clone https://github.com/jokoframework/joko_backend_starter_kit.git
$ cd joko_backend_starter_kit
```
## Configuración de settings.xml
Una vez terminado el paso anterior (Clonar proyecto), ir a su carpeta personal `/home/username`, abrir terminal y ejecutar lo siguiente:

```shell
# Si no existe el directorio, crearlo y luego cambiarse al directorio
$ mkdir .m2

$ cd .m2
```

Cuando estemos en la carpeta .m2, se debe crear un archivo vacío llamado `settings.xml` y pegar lo siguiente dentro del archivo:

```xml
  <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>

  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>central</id>
          <url>https://repo1.maven.org/maven2</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
        
        <repository>
          <id>github</id>
          <name>GitHub jokoframework Apache Maven Packages</name>
          <url>https://maven.pkg.github.com/jokoframework/security</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>PERSONAL ACCESS TOKEN</password>
    </server>
  </servers>
</settings>
```

`USERNAME` corresponde tu usuario de GitHub y el `PERSONAL ACCESS TOKEN` corresponde al token de accesso personal de dicho usuario. En caso de no tener un token, se puede crear uno siguiendo la siguiente guía https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token

Esto permite utilizar los github packages para obtener las dependencias de joko-utils y [security](https://github.com/jokoframework/security)  

Para probar que se puede compilar el proyecto ejecutar en el directorio clonado con el git
Esto demorará unos minutos en su primera ejecución; la descarga de las librerías necesarias se realiza sólo la primera vez. 

```shell
$ mvn compile
```

## Ejecutar el proyecto de backend

### Opción 1: Ejecución Con Docker
La forma más simple de levantar el proyecto es con la utilización de Docker.
Para esto debes copiar el archivo de ejemplo env.sample ejecutando el siguiente comando dentro del proyecto:
```shell
cp env.demo .env
``` 
Ahí debes cambiar las variables `APPLICATION_ROOT_FOLDER` y `MAVEN_SETTINGS_FOLDER` para que apunten a los directorios en tu equipo.

Finalmente levantamos la aplicación con el comando
```shell
docker-compose up
``` 

ó si ya se está usando la versión del compose como plugin

`docker compose up`

### Opción 2: Ejecución desde el JAR empaquetado
Se debe ejecutar lo siguiente dentro del proyecto:
```shell
mvn clean package
cd target/
java -jar joko-backend-starter-kit-1.0.7.jar
```

## Ejecución Normal
Si todavía no tenemos ninguna de las librerías instaladas (joko-utils y [security](https://github.com/jokoframework/security)) entonces se procede con una de las siguientes opciones:

### Opción 1 - Correr con Maven

Posteriormente, nos vamos a la carpeta del proyecto, abrimos terminal y ejecutamos lo siguiente para levantar el proyecto:

```shell
  $ mvn spring-boot:run
```

El usuario/password default que se crea con la base de datos, es admin/123456

El proyecto usa por default la base de datos Embedded h2. 

### Opción 2 - Correr con STS IDE

Primero se abre la carpeta del proyecto en el STS y esperamos a que descargue todas las dependencias necesarias.

Una vez terminado el proceso (puede tardar unos minutos) le damos a RUN AS y luego SPRING BOOT APP.

La mayoría de los IDEs soportan ejecución de aplicaciones tipo Spring Boot o 
permiten configurar ejecuciones customizadas de maven.

## Swagger API

El proyecto cuenta con documentación del API accesible desde el swagger-ui.
URI al swagger desde máquina HOST:

   http://localhost:8080/swagger-ui/
	
OBS. Si se desea abrir la página desde algún Windows u otro SO interno:

   http://"IP DE LA MAQUINA HOST":8080/swagger-ui/
