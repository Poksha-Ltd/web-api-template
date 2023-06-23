#! /bin/bash -e

# テスト用のメールアドレスを UNIXTIME で生成
DUMMY_EMAIL=$(date -j +%s)
echo "DUMMY_EMAIL[$DUMMY_EMAIL] used by this test"


echo "===check signUp result is success==="
USER_ID=$(curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"${DUMMY_EMAIL}\", \"password\":\"1111\"}" "localhost:8080/v1/auth/signUp/emailPassword" | jq -r ".data.id")
echo "signUp user [$USER_ID] is success!!!"


echo '===check signUp result is UserAlreadyExists when user is already registered==='
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"$DUMMY_EMAIL\", \"password\":\"1111\"}" "localhost:8080/v1/auth/signUp/emailPassword" | jq


echo '===check signIn result is success==='
# curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"$DUMMY_EMAIL\", \"password\":\"1111\"}" "localhost:8080/v1/auth/signIn/emailPassword" | jq ".data.token.accessToken"
TOKEN=$(curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"$DUMMY_EMAIL\", \"password\":\"1111\"}" localhost:8080/v1/auth/signIn/emailPassword | jq -r ".data.token.accessToken")
echo "signIn user [$USER_ID] success!!!"


echo '===check signIn result is AuthenticationFailed when password is wrong==='
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"$DUMMY_EMAIL\", \"password\":\"9999\"}" localhost:8080/v1/auth/signIn/emailPassword | jq


echo '===check signIn result is UserNotFound when email is not registered==='
curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"UserNotFound\", \"password\":\"1111\"}" localhost:8080/v1/auth/signIn/emailPassword | jq


echo '===check updating password is success==='
curl -s -X PATCH -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" localhost:8080/v1/auth/users/$USER_ID/password -d "{\"id\": \"$USER_ID\",\"password\": \"9999\"}" | jq


echo "===check signIn result is success when updated password is used==="
UPDATED_TOKEN=$(curl -s -X POST -H "Content-Type: application/json" -d "{\"email\":\"$DUMMY_EMAIL\", \"password\":\"9999\"}" localhost:8080/v1/auth/signIn/emailPassword | jq -r ".data.token.accessToken")
echo 'signIn success!!!'


echo "===check updating password is forbidden when url user id does not match payload user id==="
DUMMY_ID="da868be4-83d2-4ad8-9150-10b946631953"
curl -s -i -X PATCH -H "Content-Type: application/json" -H "Authorization: Bearer $UPDATED_TOKEN" localhost:8080/v1/auth/users/$DUMMY_ID/password -d "{\"id\": \"$USER_ID\",\"password\": \"9999\"}" | head -n 1
