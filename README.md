# Simple redis client

[![Keep a Changelog v1.1.0 badge](https://img.shields.io/badge/changelog-Keep%20a%20Changelog%20v1.1.0-%23E05735)](https://github.com/fugerit-org/fj-client-redis/blob/master/CHANGELOG.md) 
[![Maven Central](https://img.shields.io/maven-central/v/org.fugerit.java/fj-client-redis.svg)](https://mvnrepository.com/artifact/org.fugerit.java/fj-client-redis)
[![license](https://img.shields.io/badge/License-Apache%20License%202.0-teal.svg)](https://opensource.org/licenses/Apache-2.0)
[![code of conduct](https://img.shields.io/badge/conduct-Contributor%20Covenant-purple.svg)](https://github.com/fugerit-org/fj-universe/blob/main/CODE_OF_CONDUCT.md)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=fugerit-org_fj-client-redis&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=fugerit-org_fj-client-redis)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=fugerit-org_fj-client-redis&metric=coverage)](https://sonarcloud.io/summary/new_code?id=fugerit-org_fj-client-redis)

[![Java runtime version](https://img.shields.io/badge/run%20on-java%2011+-%23113366.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Java build version](https://img.shields.io/badge/build%20on-java%2011+-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://universe.fugerit.org/src/docs/versions/java11.html)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-3.9.0+-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://universe.fugerit.org/src/docs/versions/maven3_9.html)

*Status*  
Currently support only a single set or get operation at command line or a simple GUI


## Quickstart

### Build

`mvn clean install -P singlepackage`

or build & test : 

`mvn clean install -P test,singlepackage`

NOTE: tests needs containers environment available (through testcontainers).
  So are disabled by default and cam be enabled adding the 'test' profile.

### Run

- set : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --key ${KEY-TO-SET} --value ${VALUE-TO-SET}`
- get : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --key ${KEY-TO-SEARH}`
 -gui : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --mode gui`


## Running redis test container

The client had been tested running redis on [docker hub](https://hub.docker.com/_/redis) : 

### Creating Redis 7 container :

```
docker run -p 6379:6379 --name REDIS7 -d redis:7.2-alpine redis-server --save 60 1 --loglevel warning
```

### Or creating Redis 5 container :  

```
docker run -p 6379:6379 --name REDIS5 -d redis:5.0-alpine redis-server --save 60 1 --loglevel warning` 
```

## Sample commands on local container : 

### test set : 

```
java -jar target/dist-fj-client-redis-*.jar --redis-url redis://localhost --key test-key-1 --value test-value-1
```

### test get :

```
java -jar target/dist-fj-client-redis-*.jar --redis-url redis://localhost --key test-key-1
```

### test gui :

```
java -jar target/dist-fj-client-redis-*.jar --redis-url redis://localhost --mode gui
```
