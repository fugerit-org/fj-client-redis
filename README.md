# Simple redis client

See [CHANGELOG](CHANGELOG.md)

*Status*  
Currently support only connection test  

*Requirement*
* JDK 11+ (tested with oracle jdk 11.0.16.1)
* Apache Maven 3.8+ (tested with 3.8.6)

*Quickstart*   
* build :  mvn clean install -P singlepackage
* run : java -jar target/dist-fj-client-redis-${VERSION}.jar --redis-url ${REDIS-URL} --test-key ${KEY-TO-SEARH}

