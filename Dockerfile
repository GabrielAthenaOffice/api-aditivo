FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia o JAR para um nome fixo (evita problemas com versões)
COPY target/*.jar app.jar

# Cria um usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

EXPOSE 8080

# Otimizações para container
ENTRYPOINT ["java", "-jar", "/app/app.jar"]