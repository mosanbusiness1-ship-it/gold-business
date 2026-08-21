# Étape 1 : Compilation (Inchangée car elle fonctionne)
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY . .

RUN mvn clean && \
    rm -rf /root/.m2/repository/xml-apis && \
    ./mvnw package -DskipTests

# Étape 2 : Exécution avec Eclipse Temurin (Version stable officielle)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Récupération du fichier JAR généré
COPY --from=build /app/target/gold-business-0.0.1-SNAPSHOT.jar app.jar

# Déclaration de la variable d'environnement
ENV APP_KAFKA_ENABLED=true

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

