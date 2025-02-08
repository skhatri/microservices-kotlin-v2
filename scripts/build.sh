#!/usr/bin/env bash

cmd=$1

runServerAndTest(){
  local sub=$1
  if [[ $sub == "rebuild" ]];
  then
    docker compose build starter
  fi;
  docker compose up postgres starter -d
  attempt=0
  test=0
  while [[ true ]];
  do
    curl -sk https://localhost:8080/health
    if [[ $? -eq 0 ]];
    then
      echo "server is running. starting test"
      test=1
      break;
    fi;
    attempt=$((attempt + 1))
    sleep 1;
    if [[ $attempt -gt 15 ]];
    then
      break;
    fi;
  done;

  if [[ $test -eq 1 ]];
  then
    ./gradlew integration-test:test
    docker compose down
  else
    echo "server did not start."
  fi;
}

case $cmd in
  integrationTest)
    shift 1;
    runServerAndTest $*
    ;;
  *)
    echo "command not supported"
    exit 1;
    ;;
esac