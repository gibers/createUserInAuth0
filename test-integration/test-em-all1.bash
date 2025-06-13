#!/usr/bin/env bash
#
# Sample usage:
#
# HOST=localhost PORT=7000 ./test-integration/test-em-all1.bash
#
: ${HOST=localhost}
: ${PORT=8443}
: ${AUTH0_MANAGEMENT_API_CLIENT=application_client_id}
: ${AUTH0_MANAGEMENT_API_CLIENTSECRET=application_client_secret}
: ${AUTH0_DOMAIN=your_tenant}
: ${PROD_ID_REVS_RECS=1}
: ${PROD_ID_NOT_FOUND=13}
: ${PROD_ID_NO_RECS=113}
: ${PROD_ID_NO_REVS=213}


if [ -f .env ]; then
    source .env
    source test-integration/.env-test
fi

urlencode() {
    # Vérifie si un argument a été fourni
    if [ -z "$1" ]; then
        echo "Usage: urlencode <string>"
        return 1
    fi

    # Vérifie si jq est installé
    if ! command -v jq &> /dev/null; then
        echo "Error: jq is not installed. Please install it first."
        return 1
    fi

    # Encode la chaîne avec jq
    echo "$1" | jq -rR '@uri'
}

# Exemples d'utilisation :
# urlencode "hello world"        # Résultat : hello%20world
# urlencode "test|pipe"         # Résultat : test%7Cpipe
# urlencode "user@example.com"  # Résultat : user%40example.com

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]
  then
    if [ "$httpCode" = "200" ]
    then
      echo "Test OK (HTTP Code: $httpCode)"
    else
      echo "Test OK (HTTP Code: $httpCode, $RESPONSE)"
    fi
  else
    echo  "Test FAILED, EXPECTED HTTP Code: $expectedHttpCode, GOT: $httpCode, WILL ABORT!"
    echo  "- Failing command: $curlCmd"
    echo  "- Response Body: $RESPONSE"
    exit 1
  fi
}


ACCESS_TOKEN=$(curl -s --request POST \
  --url ${AUTH0_DOMAIN}/oauth/token \
  --header 'content-type: application/json' \
  --data "{
    \"client_id\":\"${AUTH0_MANAGEMENT_API_CLIENT}\",
    \"client_secret\":\"${AUTH0_MANAGEMENT_API_CLIENTSECRET}\",
    \"audience\":\"${AUTH0_DOMAIN}/api/v2/\",
    \"grant_type\":\"client_credentials\"
  }" \
| jq .access_token -r )

assertCurl 200 "curl http://localhost:8080/api/hello -s"

#1. create a user in auth0
# assertCurl 200 "curl --request POST http://localhost:8080/users/create -s"

USER_DETAIL_JUSTCREATED=$(curl -sL 'http://localhost:8080/users/create' \
    -H 'Content-Type: application/json' \
    --data-raw "{
      \"email\": \"$USERTESTFORCREATION_EMAIL\",
      \"phone_number\": \"\",
      \"user_metadata\": {},
      \"blocked\": false,
      \"email_verified\": false,
      \"phone_verified\": false,
      \"app_metadata\": {},
      \"given_name\": \"\",
      \"family_name\": \"\",
      \"name\": \"\",
      \"nickname\": \"\",
      \"picture\": \"\",
      \"user_id\": \"\",
      \"connection\": \"$USERTESTFORCREATION_CONNECTION\",
      \"password\": \"$USERTESTFORCREATION_PASSWORD\",
      \"verify_email\": false,
      \"username\": \"\"
    }")
# echo "res=$res"
if [ -z $(echo $USER_DETAIL_JUSTCREATED | jq -r '.user_id') ]
then
  echo "endpoint POST /users/create failed"
  exit
else
  echo "endpoint POST /users/create OK"
fi


#2.1 get the token of the user
USER_AT=$(curl -s --request POST \
  --url ${AUTH0_DOMAIN}/oauth/token \
  --header 'content-type: application/json' \
  --data "{
    \"grant_type\": \"password\",
    \"username\": \"${USERTESTFORCREATION_EMAIL}\",
    \"password\": \"${USERTESTFORCREATION_PASSWORD}\",
    \"client_id\": \"${SINGLE_PAGE_APPLICATION_CLIENT_ID}\",
    \"client_secret\": \"${SINGLE_PAGE_APPLICATION_PASSWORD}\",
    \"audience\": \"${OKTA_OAUTH2_AUDIENCE}\",
    \"scope\": \"openid profile email\"
}" | jq .access_token -r)

# 2.2 get the user_id
USER_ID=$(curl -sL https://dev-vdq6m1xreq5jdtcb.eu.auth0.com/api/v2/users-by-email?email=$USERTESTFORCREATION_EMAIL \
-H 'Accept: application/json' -H "Authorization: Bearer ${ACCESS_TOKEN}" | jq -r .[0].user_id)

# 2. test the endpoint: /users/userId
USER_DETAIL=$(curl -sL http://localhost:8080/users/$(urlencode $USER_ID) -H "Authorization: Bearer $USER_AT")
if [ ! $(echo $USER_DETAIL | jq -r '.user_id') = $USER_ID ]
then
  echo "endpoint GET /users/{user_id} failed"
  exit
else
  echo "endpoint GET /users/{user_id} OK"
fi

# 3 test delete the user
HTTP_CODE=$(curl -sL --request DELETE http://localhost:8080/users/$(urlencode $USER_ID) -H "Authorization: Bearer $USER_AT" -w "%{http_code}")
if [ ! $HTTP_CODE = 200 ]
then
  echo "endpoint DELETE /users/{user_id} failed"
  exit
else
  echo "endpoint DELETE /users/{user_id} OK"
fi


echo "all the tests are OK"




