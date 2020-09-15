#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --password "$POSTGRES_PASSWORD" --dbname "$POSTGRES_DB" <<-EOSQL

  CREATE DATABASE authdb;
  GRANT ALL PRIVILEGES ON DATABASE authdb TO postgres;

  CREATE DATABASE gamedb;
  GRANT ALL PRIVILEGES ON DATABASE gamedb TO postgres;

  CREATE DATABASE notificationdb;
  GRANT ALL PRIVILEGES ON DATABASE notificationdb TO postgres;
EOSQL
