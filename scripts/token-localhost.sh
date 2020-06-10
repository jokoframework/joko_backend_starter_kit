#!/bin/bash
BASE_URL="http://localhost:8080"
rm -f  refresh-token.json access-token.json

curl -v ${BASE_URL}/api/login -H 'Origin: http://localhost:8080' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36' -H 'X-JOKO-SECURITY-VERSION: 1.0' -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Referer: http://localhost:8080/courier/swagger/index.html' -H 'Connection: keep-alive' --data-binary '{"username":"admin", "password":"123456" }' --compressed -o refresh-token.json
cat refresh-token.json
REFRESH=`grep secret refresh-token.json | awk -F\" '{print $10}'`


curl ${BASE_URL}/api/token/user-access -X POST -H 'Origin: http://localhost:8080' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: en-US,en;q=0.8' -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36' -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'Referer: http://localhost:8080/courier/swagger/index.html' -H 'Connection: keep-alive' -H "X-JOKO-AUTH: ${REFRESH}" -H 'Content-Length: 0' --compressed -o access-token.json

echo "Access token"
grep secret access-token.json | awk -F\" '{print $10}'
echo ""

