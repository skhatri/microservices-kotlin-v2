PWD := $(shell pwd)

integrationTestOnly:
	@./scripts/build.sh integrationTest

integrationTest:
	@./scripts/build.sh integrationTest rebuild

.PHONY: init
init:
	@./scripts/init.sh

.PHONY: build
build: init
	@./gradlew clean app:build

image: build
	@docker build --no-cache -t microservices-kotlin-v2 .

buildCompose: build
	@docker compose build starter

up: buildCompose
	@docker compose up postgres starter -d

