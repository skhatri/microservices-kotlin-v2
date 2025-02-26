PWD := $(shell pwd)
DATE := $(shell date +'%Y-%m-%d'T'%H:%M:%S%z')
COMMIT_HASH := $(shell git rev-parse --short HEAD)

.PHONY: init clean build image integrationTestOnly integrationTest

integrationTestOnly:
	@./scripts/build.sh integrationTest

integrationTest:
	@./scripts/build.sh integrationTest rebuild

init:
	@./scripts/init.sh

build: init
	@./gradlew clean build -PcommitHash=$(COMMIT_HASH) -PbuildDate=$(DATE)

image: build
	@docker build --no-cache --build-arg BUILD_DATE=$(DATE) --build-arg COMMIT_HASH=$(COMMIT_HASH) -t microservices-kotlin-v2 .

buildCompose: build
	@docker compose build starter

up: buildCompose
	@docker compose up --wait postgres starter -d

