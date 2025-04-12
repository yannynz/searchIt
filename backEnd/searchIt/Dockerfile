# Etapa 1: Build com Maven
FROM maven:3.9.6-eclipse-temurin-17 as builder
WORKDIR /app

# Copia o arquivo pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o restante do código-fonte
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime com OpenJDK
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia o .jar gerado pela etapa de build. Se o nome do jar variar, use um wildcard.
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

