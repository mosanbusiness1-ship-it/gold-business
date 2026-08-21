# Étape 1 : Utiliser exactement la même image Java que votre machine Ubuntu
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY . .

# La commande exacte qui fonctionne chez vous
RUN mvn clean package -DskipTests

# Étape 2 : Exécution avec le même environnement Java 17
FROM openjdk:17-jdk-slim
WORKDIR /app

# Récupération du fichier JAR généré
COPY --from=build /app/target/gold-business-0.0.1-SNAPSHOT.jar app.jar

# Déclaration de la variable demandée par votre application
ENV APP_KAFKA_ENABLED=false

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
