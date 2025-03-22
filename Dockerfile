FROM docker.io/library/amazoncorretto:21-alpine

COPY server/target/server*.jar /server.jar

CMD java -jar /server.jar config/yacic.json
