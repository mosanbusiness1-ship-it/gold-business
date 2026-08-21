# Étape 1 : Compilation isolée
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# Force l'utilisation d'un répertoire local temporaire pour vider le cache corrompu de Render
RUN mvn clean package -DskipTests -Dmaven.repo.local=/app/.m2/repository

# Étape 2 : Exécution avec un JRE léger
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copie du fichier JAR généré
COPY --from=build /app/target/gold-business-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
