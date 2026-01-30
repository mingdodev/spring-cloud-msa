FROM eclipse-temurin:21

VOLUME /tmp

COPY target/apigateway-service-*.jar ApigatewayService.jar

ENTRYPOINT ["java", "-jar", "ApigatewayService.jar"]