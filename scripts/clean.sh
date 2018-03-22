# DEMO DB SCRIPT
TITLE='\033[34m'
TEXT='\033[33m'
NC='\033[0m' # No Color
echo -e " ${TITLE}CLEANING :D${TEXT}"

mvn -s ~/.m2/devtools-settings.xml -Dext.prop.dir=/opt/starter-kit/ -Dspring.config.location=file:///opt/starter-kit/application.properties -DskipTests -U clean install

echo -e " ${TITLE}FINISHED${NC}"