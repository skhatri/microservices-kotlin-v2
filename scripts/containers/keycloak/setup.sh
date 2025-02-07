#!/usr/bin/env sh
IDP_HOST=keycloak

health() {
  while true;
  do
    curl -sk "http://${IDP_HOST}:8080/health/ready"
    if [[ $? -eq 0 ]];
    then
      break;
    else
      echo "not ready. waiting..."
    fi;
    sleep 5;
  done;
}

health

ACCESS_TOKEN=$(curl -sk "http://${IDP_HOST}:8080/realms/master/protocol/openid-connect/token" -d 'client_id=admin-cli&grant_type=password&username=admin&password=admin' |jq -r .access_token)

curl -s -H"Authorization: Bearer ${ACCESS_TOKEN}" http://${IDP_HOST}:8080/admin/realms/portals

curl -s -X DELETE -H"Authorization: Bearer ${ACCESS_TOKEN}" http://${IDP_HOST}:8080/admin/realms/portals

curl -s -X PUT -H"Content-Type: application/json" \
        -H"Authorization: Bearer ${ACCESS_TOKEN}" http://${IDP_HOST}:8080/admin/realms/master \
        -d '{"realm": "master",  "accessTokenLifespan": 1600,  "ssoSessionIdleTimeout": 1800, "enabled": true}'

curl -s -X POST -H"Content-Type: application/json" \
        -H"Authorization: Bearer ${ACCESS_TOKEN}" http://${IDP_HOST}:8080/admin/realms \
        -d '{"realm": "portals",  "accessTokenLifespan": 1600, "ssoSessionIdleTimeout": 1800, "enabled": true}'

curl -s -H"Authorization: Bearer ${ACCESS_TOKEN}" "http://${IDP_HOST}:8080/admin/realms/portals/clients?clientId=next-js"
curl -s -H"Authorization: Bearer ${ACCESS_TOKEN}" -X DELETE "http://${IDP_HOST}:8080/admin/realms/portals/clients/0e199f47-f09d-4eee-9b9c-8a8afa839648"

export CLIENT_ID="next-js" 
export CLI_SECRET="pLQdNhs5gXvv2PnON30pAvt0s9zaG9bB"

curl -s -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" "http://${IDP_HOST}:8080/admin/realms/portals/clients" -d '  {
    "id": "0e199f47-f09d-4eee-9b9c-8a8afa839648",
    "clientId": "next-js",
    "name": "next-js",
    "description": "next js client",
    "rootUrl": "http://sampleapp:3000",
    "adminUrl": "http://sampleapp:3000",
    "baseUrl": "http://sampleapp:3000",
    "surrogateAuthRequired": false,
    "enabled": true,
    "alwaysDisplayInConsole": false,
    "clientAuthenticatorType": "client-secret",
    "secret": "pLQdNhs5gXvv2PnON30pAvt0s9zaG9bB",
    "redirectUris": [
      "http://sampleapp:3000/*"
    ],
    "webOrigins": [
      "*"
    ],
    "notBefore": 0,
    "bearerOnly": false,
    "consentRequired": false,
    "standardFlowEnabled": true,
    "implicitFlowEnabled": false,
    "directAccessGrantsEnabled": true,
    "serviceAccountsEnabled": true,
    "authorizationServicesEnabled": true,
    "publicClient": false,
    "frontchannelLogout": true,
    "protocol": "openid-connect",
    "attributes": {
      "oidc.ciba.grant.enabled": "false",
      "oauth2.device.authorization.grant.enabled": "true",
      "client.secret.creation.time": "1689683325",
      "backchannel.logout.session.required": "true",
      "backchannel.logout.revoke.offline.tokens": "false"
    },
    "authenticationFlowBindingOverrides": {},
    "fullScopeAllowed": true,
    "nodeReRegistrationTimeout": -1,
    "protocolMappers": [
      {
        "id": "9fbf4d84-a038-422c-82cf-1c1fb14eddf4",
        "name": "Client Host",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "config": {
          "user.session.note": "clientHost",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "clientHost",
          "jsonType.label": "String"
        }
      },
      {
        "id": "0936c337-127b-4fb8-98f8-24c6926d8b3b",
        "name": "Client ID",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "config": {
          "user.session.note": "client_id",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "client_id",
          "jsonType.label": "String"
        }
      },
      {
        "id": "a1be9125-f832-406c-8770-cd97547c0c79",
        "name": "Client IP Address",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-usersessionmodel-note-mapper",
        "consentRequired": false,
        "config": {
          "user.session.note": "clientAddress",
          "id.token.claim": "true",
          "access.token.claim": "true",
          "claim.name": "clientAddress",
          "jsonType.label": "String"
        }
      },
      {
        "id": "ffd72d84-9fb2-49a9-9200-b71ab6eb3491",
        "name": "groups-mapper",
        "protocol": "openid-connect",
        "protocolMapper": "oidc-group-membership-mapper",
        "consentRequired": false,
        "config": {
            "full.path": "true",
            "id.token.claim": "true",
            "access.token.claim": "true",
            "claim.name": "groups",
            "userinfo.token.claim": "true"
        }
      }      
    ],
    "defaultClientScopes": [
      "web-origins",
      "acr",
      "roles",
      "profile",
      "email"
    ],
    "optionalClientScopes": [
      "address",
      "phone",
      "offline_access",
      "microprofile-jwt"
    ],
    "access": {
      "view": true,
      "configure": true,
      "manage": true
    }
  }'


export PORTAL_ADMIN_URL="http://${IDP_HOST}:8080/admin/realms/portals"

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users -d '{
"username": "user1",
"enabled": true,
"totp": false,
"emailVerified": true,
"firstName": "User",
"lastName": "One",
"email": "user1@gmail.com",
"disableableCredentialTypes": [],
"requiredActions": [],
"notBefore": 0,
"access": {
    "manageGroupMembership": true,
    "view": true,
    "mapRoles": true,
    " impersonate": true,
    "manage": true
    }
}'

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users -d '{
"username": "user2",
"enabled": true,
"totp": false,
"emailVerified": true,
"firstName": "User",
"lastName": "Two",
"email": "user2@gmail.com",
"disableableCredentialTypes": [],
"requiredActions": [],
"notBefore": 0,
"access": {
    "manageGroupMembership": true,
    "view": true,
    "mapRoles": true,
    " impersonate": true,
    "manage": true
    }
}'

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users -d '{
"username": "user3",
"enabled": true,
"totp": false,
"emailVerified": true,
"firstName": "User",
"lastName": "Three",
"email": "user3@gmail.com",
"disableableCredentialTypes": [],
"requiredActions": [],
"notBefore": 0,
"access": {
    "manageGroupMembership": true,
    "view": true,
    "mapRoles": true,
    " impersonate": true,
    "manage": true
    }
}'

user1=$(curl -sk -H"Authorization: Bearer ${ACCESS_TOKEN}" "${PORTAL_ADMIN_URL}/users?username=user1"|jq -r '. [0].id') 
user2=$(curl -sk -H"Authorization: Bearer ${ACCESS_TOKEN}" "${PORTAL_ADMIN_URL}/users?username=user2"|jq -r '.[0].id') 
user3=$(curl -sk -H"Authorization: Bearer ${ACCESS_TOKEN}" "${PORTAL_ADMIN_URL}/users?username=user3"|jq -r '.[0].id')

curl -s -XPUT -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/${user1}/reset-password -d '{
    "type": "password",
    "value": "password",
    "temporary": false
}'

curl -s -XPUT -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/${user2}/reset-password -d '{
    "type": "password",
    "value": "password",
    "temporary": false
}'

curl -XPUT -s -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/${user3}/reset-password -d '{
    "type": "password",
    "value": "password",
    "temporary": false
}'

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/groups 

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/groups -d '{
    "name": "WebAdmin",
    "path": "/WebAdmin",
    "subGroups": []
}'

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/groups -d '{
    "name": "WebUser",
    "path": "/WebUser",
    "subGroups": []
}'

webuser_id=$(curl -sk H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" "${PORTAL_ADMIN_URL}/groups?search=WebUser"|jq -r '.[0].id')
webadmin_id=$(curl -sk H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" "${PORTAL_ADMIN_URL}/groups?search=WebAdmin"|jq -r '.[0].id')


curl -sk -X PUT -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/$user1/groups/${webadmin_id}
curl -sk -X PUT -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/$user2/groups/${webadmin_id}
curl -sk -X PUT -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/$user3/groups/${webuser_id}

curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/users/$user1/groups


curl -sk -H"Content-Type: application/json" -H"Authorization: Bearer ${ACCESS_TOKEN}" ${PORTAL_ADMIN_URL}/clients


export CLIENT_ID="next-js"
export CLI_SECRET="pLQdNhs5gXvv2PnON30pAvt0s9zaG9bB"
export IDP_HOST=keycloak

curl -s \
-d "client_id=${CLIENT_ID}" \
-d "client_secret=${CLI_SECRET}" \
-d "username=user1" \
-d "password=password" \
-d "grant_type=password" \
-d "scope=openid" \
"http://${IDP_HOST}:8080/realms/portals/protocol/openid-connect/token"


curl -s http://${IDP_HOST}:8080/realms/portals/.well-known/openid-configuration
curl -s http://${IDP_HOST}:8080/realms/portals/protocol/saml/descriptor

USER_ACCESS_TOKEN=$(curl -s \
-d "client_id=${CLIENT_ID}" \
-d "client_secret=${CLI_SECRET}" \
-d "username=user1" \
-d "password=password" \
-d "grant_type=password" \
-d "scope=openid" \
"http://${IDP_HOST}:8080/realms/portals/protocol/openid-connect/token" |jq -r '.access_token')


curl -s -H"Authorization: Bearer ${USER_ACCESS_TOKEN}" http://keycloak:8080/realms/portals/protocol/openid-connect/userinfo

