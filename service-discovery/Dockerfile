FROM eclipse-temurin:21

VOLUME /tmp

COPY target/service-discovery-*.jar ServiceDiscovery.jar

ENTRYPOINT ["java", "-jar", "ServiceDiscovery.jar"]