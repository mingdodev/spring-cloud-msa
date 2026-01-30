FROM eclipse-temurin:21

VOLUME /tmp

COPY target/order-service-*.jar OrderService.jar

ENTRYPOINT ["java", "-jar", "OrderService.jar"]