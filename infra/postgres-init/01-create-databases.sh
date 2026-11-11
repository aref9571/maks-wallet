#!/bin/bash
set -euo pipefail

psql -v ON_ERROR_STOP=1 \
  --username "$POSTGRES_USER" \
  --dbname "$POSTGRES_DB" \
  -v maks_password="$MAKS_APP_PASSWORD" \
  -v risk_password="$RISK_APP_PASSWORD" <<'EOSQL'
CREATE ROLE maks_app LOGIN PASSWORD :'maks_password';
CREATE ROLE risk_app LOGIN PASSWORD :'risk_password';
CREATE DATABASE maks OWNER maks_app;
CREATE DATABASE risk OWNER risk_app;
REVOKE CONNECT ON DATABASE maks FROM PUBLIC;
REVOKE CONNECT ON DATABASE risk FROM PUBLIC;
EOSQL
