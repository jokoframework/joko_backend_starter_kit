#!/bin/bash
set -e
set -x

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE joko_db;
    CREATE USER joko_user WITH PASSWORD 'joko_password';
    ALTER USER joko_user WITH CREATEDB;
    ALTER DATABASE joko_db OWNER TO joko_user;
    GRANT ALL PRIVILEGES ON DATABASE joko_db TO joko_user;
EOSQL
