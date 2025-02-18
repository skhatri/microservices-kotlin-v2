#!/usr/bin/env bash

: "${cmd:=${1:-app}}"

case $cmd in

otel)
  docker compose --profile=otel up -d
  ;;
down)
  docker compose --profile="*" down
  ;;
*)
  docker compose --profile=app up -d
  ;;
esac

