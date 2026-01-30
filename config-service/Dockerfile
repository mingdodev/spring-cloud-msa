FROM eclipse-temurin:21

VOLUME /tmp

COPY apiEncryptionKey.jks apiEncryptionKey.jks

RUN chmod 644 /apiEncryptionKey.jks

COPY target/config-service-*.jar ConfigService.jar

ENTRYPOINT ["java", "-jar", "ConfigService.jar"]