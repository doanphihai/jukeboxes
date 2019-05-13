FROM openjdk:8-jdk-alpine
VOLUME /tmp

COPY ./target/jukeboxes-0.0.1-SNAPSHOT.jar app.jar

# For Tomcat
EXPOSE 9090:8080/tcp

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./jukeboxes", "-Dspring.data.cassandra.contact-points=192.168.99.100", "-Dspring.data.cassandra.port=9045", "-jar", "/app.jar"]
