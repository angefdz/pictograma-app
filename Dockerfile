# Usa una imagen de Maven con JDK 17 para compilar
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Copia los archivos del proyecto y compila
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Usa una imagen liviana con Java 17 para ejecutar el jar
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/tfg-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Ejecuta el .jar
ENTRYPOINT ["java", "-jar", "app.jar"]
