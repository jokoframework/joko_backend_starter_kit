#!/bin/bash

# Este archivo genera un conjunto de archivos liquidbase en base a la estructura de una 
# base de datos.
# Se debe configurar la variable SCHEMAS con la lista de schemas disponibles

OUTPUT_DIRECTORY=generatedSchema
DRIVER_PATH=$HOME/.m2/repository/org/postgresql/postgresql/9.4-1205-jdbc41/postgresql-9.4-1205-jdbc41.jar
SCHEMAS=(basic )

#Crea el directorio de output y se asegura que este vacio
mkdir -p $OUTPUT_DIRECTORY
rm -f $OUTPUT_DIRECTORY/*.xml

for schema in "${SCHEMAS[@]}"
do  
    echo "Generating schema $schema..."

    liquibase --driver=org.postgresql.Driver \
      --classpath=$DRIVER_PATH \
      --changeLogFile=$OUTPUT_DIRECTORY/db.changelog-reverse-$schema.xml \
      --url=jdbc\:postgresql\://localhost\:5432/starter_kit \
      --username=postgres \
      --password=postgres \
      --defaultSchemaName=$schema \
      generateChangeLog

    echo ""
done  

