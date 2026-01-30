FROM eclipse-temurin:21

VOLUME /tmp

COPY target/user-service-*.jar UserService.jar

ENTRYPOINT ["java", "-jar", "UserService.jar"]