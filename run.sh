#!/usr/bin/env bash

: "${cmd:=${1:-app}}"

unittest(){
 : "${pattern:=${1:-"*Test*"}}"
 ./gradlew app:test --tests="${pattern}"
}

case $cmd in

otel)
  docker compose --profile=otel up -d
  ;;
db)
  docker compose --profile=db up -d
  ;;
down)
  docker compose --profile="*" down
  ;;
test)
  shift 1;
  unittest $*
  ;;
deptree)
  shift 1;
  ./scripts/insight.sh ${1}
  ;;
*)
  docker compose --profile=app up -d
  ;;
esac

