# Simple redis client

See [CHANGELOG](CHANGELOG.md)

*Status*  
Currently support only a single set or get operation at command line or a simple GUI

*Requirement*
* JDK 11+ (tested with oracle jdk 11.0.16.1)
* Apache Maven 3.8+ (tested with 3.8.6)

*Quickstart*   
* build :  mvn clean install -P singlepackage
* set : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --key ${KEY-TO-SET} --value ${VALUE-TO-SET}`
* get : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --key ${KEY-TO-SEARH}`
* gui : `java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --mode gui`

*Running redis*   
The client had been tested running redis on [docker](https://hub.docker.com/_/redis).
* example of creating Redis 7 container :  `sudo docker run -p 6379:6379 --name REDIS7 -d redis:7.0.5 redis-server --save 60 1 --loglevel warning` 
* example of creating Redis 5 container :  `sudo docker run -p 6379:6379 --name REDIS5 -d redis:5.0.14 redis-server --save 60 1 --loglevel warning` 

