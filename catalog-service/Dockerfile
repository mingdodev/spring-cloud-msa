FROM eclipse-temurin:21

VOLUME /tmp

COPY target/catalog-service-*.jar CatalogService.jar

ENTRYPOINT ["java", "-jar", "CatalogService.jar"]