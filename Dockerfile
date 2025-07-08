# Etapa de construcción: usa Maven con JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución: imagen ligera con Java 17 JDK
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/target/tfg-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
