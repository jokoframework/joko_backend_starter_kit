# DEMO DB SCRIPT
TITLE='\033[34m'
TEXT='\033[33m'
NC='\033[0m' # No Color
echo -e " ${TITLE}RUNNING PROJECT WITH TESTS:D${TEXT}"

mvn -s ~/.m2/devtools-settings.xml test -Dext.prop.dir=/opt/starter-kit/ -Dspring.config.location=file:///opt/starter-kit/application.properties

echo -e " ${TITLE}FINISHED${NC}"