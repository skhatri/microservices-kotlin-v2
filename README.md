# microservices-starter

Microservices Starter Project

[![Build](https://github.com/skhatri/microservices-kotlin-v2/actions/workflows/build.yml/badge.svg)](https://github.com/skhatri/microservices-kotlin-v2/actions/workflows/build.yml)
[![Known Vulnerabilities](https://snyk.io/test/github/skhatri/microservices-starter-kotlin/badge.svg?targetFile=build.gradle.kts)](https://snyk.io/test/github/skhatri/microservices-kotlin-v2?targetFile=build.gradle.kts)

### Development

[Developer Guide](DEV.md)

### Content

| Category     | Choice     |
|--------------|------------|
| Logging      | log4j2     |
| SAST         | Sonar      |
| Tests        | JUnit5     |
| Coverage     | Jacoco     |
| Code Style   | Checkstyle |
| Load Testing | Gatling    |

### 1. data setup
We make use of some large data files which are downloaded to your local directory. Please run ```./scripts/init.sh``` to download the files used in this project.

### 2. Start Database
We will be running postgres
```
docker-compose up postgres -d
```

### load-testing
Gatling

Load test can be run using one of the following two approaches

```
gradle load-testing:runTest
IDE - com.github.starter.todo.Runner
```

### vulnerability

Install snyk and authenticate for CLI session

```
npm install -g snyk
snyk auth
```

Publish results using

```
snyk monitor --all-sub-projects
```

