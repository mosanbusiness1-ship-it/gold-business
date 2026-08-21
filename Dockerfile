# Étape 1 : Compilation avec votre version exacte de Maven (3.6.3) et Java 17
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY . .
# AJOUT DE L'OPTION -U ICI 👇
RUN mvn clean package -U -DskipTests

# Étape 2 : Exécution avec un JRE 17 léger pour la production
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copie du fichier JAR généré
COPY --from=build /app/target/gold-business-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

