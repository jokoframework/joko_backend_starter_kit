# FRESH DB INSTALL SCRIPT
TITLE='\033[34m'
YELLOW='\033[33m'
GREEN='\033[32m'
NC='\033[0m' # No Color

echo -e " ${TITLE}INSTALLING DB${NC}"

./scripts/updater fresh
echo -e " ${YELLOW}DATABASE INSTALLED${NC}"

echo -e " ${GREEN}Loading configuration data ........................................................\n${NC}"
./scripts/updater seed src/main/resources/db/sql/seed-config.sql
./scripts/updater seed src/main/resources/db/sql/seed-data.sql
./scripts/updater seed src/main/resources/db/sql/seed-articulos.sql
./scripts/updater seed src/main/resources/db/sql/seed-suppliers.sql
./scripts/updater seed src/main/resources/db/sql/seed-store.sql

echo -e " ${GREEN}FRESH INSTALATION COMPLETED${NC}"


