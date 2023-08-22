FROM maven:3.6.3-jdk-11-slim

COPY entrypoint.sh /usr/local/bin/entrypoint.sh
COPY pom.xml pom.xml
COPY src src
RUN apt-get update && apt-get install dos2unix && dos2unix /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh

# Configura variables de entorno
RUN mkdir /opt/starter-kit && mkdir /opt/starter-kit/dev
COPY src/main/resources/application.properties.example /opt/starter-kit/dev/application.properties

# Inicia la aplicación
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["bash"]
