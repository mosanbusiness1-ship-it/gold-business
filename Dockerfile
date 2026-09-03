# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Build arg pour invalider le cache sur Render quand nécessaire.
ARG CACHE_BUST=1

# Copie uniquement le pom pour optimiser les couches et éviter les builds stales.
COPY pom.xml ./
RUN rm -rf /root/.m2/repository/xml-apis /root/.m2/repository/xml-apis-ext || true
RUN mvn -B -q dependency:go-offline

# Copie du code source et build du JAR
COPY . .
RUN rm -rf /root/.m2/repository/xml-apis /root/.m2/repository/xml-apis-ext || true && \
    echo "CACHE_BUST=${CACHE_BUST}" && mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# JAR généré par Maven
COPY --from=build /app/target/gold-business-0.0.1-SNAPSHOT.jar /app/app.jar

ARG AUTO_UPDATE_DATABASE=true
ENV AUTO_UPDATE_DATABASE=${AUTO_UPDATE_DATABASE}
ENV APP_KAFKA_ENABLED=true
EXPOSE 8080

# Safe default: no schema mutation unless explicitly enabled in Docker/Render.
ENTRYPOINT ["/bin/sh", "-c", "if [ \"$AUTO_UPDATE_DATABASE\" = \"true\" ]; then export DB_HIBERNATE_DDL_AUTO=update; else export DB_HIBERNATE_DDL_AUTO=validate; fi; exec java -jar /app/app.jar --server.port=${PORT:-8080}"]

