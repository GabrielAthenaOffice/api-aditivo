FROM maven:3.9-eclipse-temurin-21 AS build

# Criar diretório de trabalho
WORKDIR /app

# Copiar apenas o POM primeiro (para cache de dependências)
COPY pom.xml .

# Baixar dependências (usa cache se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar o código fonte
COPY src ./src

# Compilar o projeto
RUN mvn clean package -DskipTests

# Stage final
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]