#!/bin/sh
COMMAND=$1


case $1 in
  up)
    COMMAND='up -d --build'
    ;;
  down)
     COMMAND='down'
    ;;
  *)
    echo "Usage: `basename $0` up|down"
    exit 1
esac

COMPOSE_FILES=' -f docker-compose.dev.yml '
docker compose ${COMPOSE_FILES} ${COMMAND}

