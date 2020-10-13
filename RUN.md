## Requisitos para ejecutar el servicio
----
* IDE compatible con proyectos MAVEN o tener instalado MAVEN en el SO de Linux que se este utilizando
* Java 11 (JDK11)
* Por default se usa la base de datos h2. Si desea usar PostgreSQL, lea [PostgreSQL.md](PostgreSQL.md)


## Clonar proyecto
----
Debe clonar el proyecto de (Es un repositorio autenticado, consultar el URL para `clone` en):

```shell
$ git clone XXXXXX
$ cd XXXX
```

### Configuración de settings.xml
----
Una vez terminado el paso anterior, ir a la carpeta `/home`, abrir terminal y ejecutar lo siguiente:

```shell
$ cd .m2
```

Cuando estemos en la carpeta oculta .m2, se debe crear un archivo vacio llamado `settings.xml` y pegar lo siguiente dentro del archivo:

```shell
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
      <password>PERSONAL ACCESS TOKEN/password>
    </server>
  </servers>
</settings>
```

`USERNAME` corresponde al usuario de GitHub y el `PERSONAL ACCESS TOKEN` corresponde al token de accesso personal de dicho usuario. En caso de no tener un token, se puede crear uno siguiendo la siguiente guía https://docs.github.com/en/free-pro-team@latest/github/authenticating-to-github/creating-a-personal-access-token

### Opcion 1 - Correr con Maven
----
Posteriormente, nos vamos a la carpeta del proyecto, abrimos terminal y ejecutamos lo siguiente para levantar el proyecto:

```shell
  $ mvn spring-boot:run
```

El usuario/password default que se crea con la base de datos, es admin/123456

El proyecto usa por default la base de datos Embedded h2. 

### Opcion 2 - Correr con STS IDE
----
Primero se abre la carpeta del proyecto en el STS y esperamos a que descargue todas las dependencias necesarias.

Una vez terminado el proceso (puede tardar unos minutos) le damos a RUN AS y luego SPRING BOOT APP.

La mayoría de los IDEs soportan ejecución de aplicaciones tipo Spring Boot o 
permiten configurar ejecuciones customizadas de maven.

### Swagger API
----
El proyecto cuenta con documentación del API accesible desde el swagger-ui.
URI al swagger desde maquina HOST:

  http://localhost:8080/swagger-ui.html
	
OBS. Si se desea abrir la pagina desde algún Windows u otro SO interno:

  http://"IP DE LA MAQUINA HOST":8080/swagger-ui.html
