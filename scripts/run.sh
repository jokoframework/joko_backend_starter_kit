# DEMO DB SCRIPT
TITLE='\033[34m'
TEXT='\033[33m'
NC='\033[0m' # No Color
echo -e " ${TITLE}RUNNING PROJECT :D${TEXT}"

source ${ENV_VARS}

export MAVEN_OPTS="-Xms256m -Xmx1024m"
mvn -s $MVN_SETTINGS spring-boot:run -Dext.prop.dir=$PROFILE_DIR/ -Dspring.config.location=file://$PROFILE_DIR/application.properties

echo -e " ${TITLE}FINISHED${NC}"
