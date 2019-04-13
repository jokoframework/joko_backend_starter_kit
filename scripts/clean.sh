# DEMO DB SCRIPT
TITLE='\033[34m'
TEXT='\033[33m'
NC='\033[0m' # No Color
echo -e " ${TITLE}CLEANING :D${TEXT}"

source ${ENV_VARS}

mvn -s $MVN_SETTINGS -Dext.prop.dir=$PROFILE_DIR/ -Dspring.config.location=file://$PROFILE_DIR/application.properties -DskipTests -U clean install

echo -e " ${TITLE}FINISHED${NC}"
