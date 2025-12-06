# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean install -DskipTests

# Estágio Final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/hospital-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "hospital-0.0.1-SNAPSHOT.jar"]
