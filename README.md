# Gold Business API

Cette application est une API Spring Boot pour gérer les organisations, produits, transactions, enchères, garanties, escrows, webhooks et la modération de contenus.

# Architecture

## Contrôleurs

Les contrôleurs exposent les routes REST utilisées par le frontend et les services externes.

## Services

Les services contiennent la logique métier, la gestion des événements Kafka, la validation, les règles d'escrow et la réconciliation des paiements.

## Dépôts

Les dépôts utilisent Spring Data JPA pour interagir avec la base de données.

# Technologies utilisées

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Kafka
- PostgreSQL
- Lombok
- OpenAPI / SpringDoc
- Testcontainers
- SLF4J avec Logback

# Prérequis

- Java 17+
- Maven
- Docker et Docker Compose (optionnel)
- PostgreSQL (et extension PostGIS si nécessaire pour les fonctionnalités géospatiales)

# Installation et configuration

## Étape 1 : Cloner le projet

```bash
git clone https://github.com/votre-utilisateur/votre-projet.git
cd votre-projet
```

## Étape 2 : Configurer la base de données

Modifiez `src/main/resources/application.properties` ou `application.yml` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/app_db
spring.datasource.username=postgres
spring.datasource.password=votre_mot_de_passe
```

## Étape 3 : Lancer l'application

```bash
./mvnw spring-boot:run
```

## Étape 4 : Accéder à l'API

Par défaut, l'API est disponible sur :

```text
http://localhost:8081
```

# Services externes et connexion

Cette application utilise plusieurs services externes : PostgreSQL, Kafka, Elasticsearch/Kibana, Redis et OAuth2.

## Base de données PostgreSQL

La configuration par défaut est dans `src/main/resources/application.properties` :

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:gold_business;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:sa}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
```

### Démarrer PostgreSQL en local

Le fichier `docker-compose.yml` fournit un service PostgreSQL :

```yaml
services:
  postgres:
    image: postgres
    container_name: gold-business-db
    environment:
      POSTGRES_DB: gold-business
      POSTGRES_USER: LENOVO
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
```

Lancer PostgreSQL :

```bash
docker compose up -d postgres
```

Si vous n'avez pas Docker (ou ne voulez pas lancer Postgres), la configuration ci-dessus utilise H2 en mémoire par défaut : l'application démarrera sans base externe.

Pour repasser à PostgreSQL ultérieurement :

1. Arrêtez l'application.
2. Définissez les variables d'environnement :

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gold-business
export SPRING_DATASOURCE_USERNAME=LENOVO
export SPRING_DATASOURCE_PASSWORD=root
```

3. Redémarrez l'application. Si vous préférez, vous pouvez aussi décommenter la section PostgreSQL dans `src/main/resources/application.properties`.


## Kafka

L'application attend Kafka sur `localhost:9092` via `src/main/resources/application.yml` :

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

### Démarrer Kafka en local

Kafka n'est pas fourni dans le `docker-compose.yml` principal, mais vous pouvez lancer un cluster local simple :

```bash
docker run -d --name zookeeper -p 2181:2181 \
  -e ALLOW_PLAINTEXT_LISTENER=yes bitnami/zookeeper:latest

docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e ALLOW_PLAINTEXT_LISTENER=yes bitnami/kafka:latest
```

Ou utilisez la distribution Confluent :

```bash
docker run -d --name confluent-kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  confluentinc/cp-kafka
```

## Elasticsearch et Kibana

Le `docker-compose.yml` contient également Elasticsearch et Kibana :

```bash
docker compose up -d elasticsearch kibana
```

Accès :

- Elasticsearch : `http://localhost:9200`
- Kibana : `http://localhost:5601`

## Redis (optionnel)

La configuration Redis est lue depuis `application.properties` :

```properties
spring.data.redis.host=${SPRING_REDIS_HOST:localhost}
spring.data.redis.port=${SPRING_REDIS_PORT:6379}
```

Si vous utilisez Redis localement :

```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

## Variables d'environnement utiles

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gold-business
export SPRING_DATASOURCE_USERNAME=LENOVO
export SPRING_DATASOURCE_PASSWORD=root
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
export JWT_SECRET_KEY=change-me
export APP_ENCRYPTION_KEY=R29sZEJ1c2luZXNzS2V5MTI=
```

## Vérification rapide

- PostgreSQL : `psql -h localhost -U LENOVO -d gold-business`
- Kafka : `kafka-topics.sh --bootstrap-server localhost:9092 --list`
- Elasticsearch : `curl http://localhost:9200`

# Intégration Frontend

## Principes

- Toutes les requêtes authentifiées doivent envoyer l'en-tête `Authorization: Bearer <token>`.
- Les endpoints publics commencent par `/public/*`.
- Les endpoints métier commencent par `/api/*`.
- Les actions liées aux escrows, commissions, garanties, webhooks et modération sont exposées dans le contrôleur `OrganisationController`.

## Authentification

- `POST /public/signup` : inscription d'un utilisateur.
- `POST /public/login` : connexion et génération de JWT.
- `POST /public/login/2fa` : validation du login 2FA si activé.
- `DELETE /public/logout` : déconnexion et révocation de token.
- `GET /public/res` : vérifie que l'utilisateur est authentifié.

### Exemple de connexion

```bash
curl -X POST http://localhost:8080/public/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret"}'
```

### Exemple d'inscription

```bash
curl -X POST http://localhost:8080/public/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"secret","fullName":"Jean Dupont"}'
```

## Organisations

- `POST /api/organisations?ownerId={ownerId}` : créer une organisation.
- `GET /api/organisations/all` : lister toutes les organisations.
- `GET /api/organisations/{id}` : obtenir les détails d'une organisation.
- `PUT /api/organisations/{id}` : mettre à jour une organisation.
- `DELETE /api/organisations/{id}` : supprimer une organisation.
- `GET /api/organisations/roots` : récupérer les organisations racines.
- `GET /api/organisations/{id}/hierarchy` : récupérer la hiérarchie.
- `PATCH /api/organisations/{id}/parent?newParentId={newParentId}` : changer le parent.

## Gestion des produits d'une organisation

- `POST /api/organisations/{orgId}/products?productId={productId}` : ajouter un produit existant à l'organisation.
- `GET /api/organisations/{orgId}/products?type={type}&minPrice={minPrice}` : lister les produits d'une organisation.
- `DELETE /api/organisations/{orgId}/products/{productId}` : retirer un produit d'une organisation.
- `POST /api/organisations/{orgId}/products/{productId}/score` : noter un produit pour l'organisation.
- `GET /api/organisations/{orgId}/products/{productId}/score` : obtenir la note d'un produit.

## Recherche et statistiques

- `GET /api/organisations/search?name={name}&type={type}&minProductCount={minProductCount}`
- `GET /api/organisations/{id}/stats`

## Avis et notes

- `POST /api/organisations/{orgId}/reviews` : ajouter un avis.
- `GET /api/organisations/{orgId}/reviews?verifiedOnly={false}&page={0}&size={10}` : lister les avis.
- `GET /api/organisations/{orgId}/ratings` : obtenir le résumé des notes.

## Validation et modération

- `POST /api/organisations/{orgId}/products/{productId}/submit` : soumettre un produit pour validation.
- `POST /api/organisations/{orgId}/validateProduct/{productId}` : approuver ou rejeter un produit soumis.
- `GET /api/organisations/{orgId}/moderation-queue` : récupérer la file de modération.
- `GET /api/organisations/{orgId}/sla-exceeded` : récupérer les éléments dépassant le SLA.

## Commissions

- `POST /api/organisations/{orgId}/commission/config` : configurer les commissions pour une organisation.

## Garantie

- `POST /api/organisations/{orgId}/guarantee` : configurer une politique de garantie.
- `POST /api/organisations/{orgId}/guarantee/claims` : créer une demande de réclamation.
- `GET /api/organisations/{orgId}/guarantee/claims` : lister les réclamations.
- `POST /api/organisations/{orgId}/guarantee/claims/{claimId}/resolve` : résoudre une réclamation.

## Escrow et paiement

- `POST /api/organisations/{orgId}/escrow` : créer une transaction d'escrow et bloquer les fonds.
- `POST /api/organisations/{orgId}/escrow/{escrowId}/release` : libérer un escrow.
- `POST /api/organisations/{orgId}/escrow/{escrowId}/refund` : rembourser un escrow.
- `GET /api/organisations/{orgId}/escrows` : lister les transactions d'escrow (idempotent pour polling frontend).

### Exemple création d'escrow

```bash
curl -X POST http://localhost:8080/api/organisations/12/escrow \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":451,"amount":12000.00,"currency":"XOF","metadata":"{\"orderId\":\"ORD-1234\"}"}'
```

## Webhooks

- `POST /api/organisations/{orgId}/webhooks` : créer une souscription webhook.
- `GET /api/organisations/{orgId}/webhooks` : lister les souscriptions.
- `DELETE /api/organisations/{orgId}/webhooks/{id}` : supprimer une souscription.

## Transactions d'achats

- `POST /api/transactions/buy/{productId}` : acheter un produit.
- `POST /api/transactions/{transactionId}/complete` : marquer une transaction comme terminée.
- `POST /api/transactions/{transactionId}/cancel` : annuler une transaction.
- `GET /api/transactions/buyer` : historique des achats du buyer.
- `GET /api/transactions/seller` : historique des ventes du seller.

### Exemple d'achat de produit

```bash
curl -X POST http://localhost:8080/api/transactions/buy/123 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

## Auto-purchase et notifications

- `auto-purchase` est géré comme un flux interne/Kafka lorsque le besoin utilisateur est configuré en `autoPurchase`.
- Le service envoie des événements Kafka `auto-purchase` et `auto-purchase-completed` pour informer les systèmes backend.
- Pour déclencher des notifications basées sur le résultat d'un auto-purchase, utilisez :
  - `POST /api/notifications/single?type={channel}`
  - `POST /api/notifications/multi`
- Ces endpoints acceptent un payload `AutoPurchaseResponse` contenant les détails du paiement, montant, devise et statut.

### Exemple de payload de notification

```json
{
  "transactionRef": "escrow-789",
  "status": "SUCCESS",
  "action": "HOLD",
  "amount": 12000.00,
  "currency": "XOF",
  "reason": null,
  "timestamp": "2026-07-18T20:00:00Z"
}
```

- `GET /public/products/{id}` : récupérer un produit.
- `PUT /public/products/{id}` : mettre à jour un produit.
- `DELETE /public/products/{id}` : supprimer un produit.
- `GET /public/products/search` : recherche simple.
- `GET /public/products/search-with-filters` : recherche avec filtres.
- `GET /public/products/export` : exporter au format CSV.
- `GET /public/products/{id}/audit` : historique de l'objet produit.
- `GET /public/products/search-elasticsearch` : recherche Elasticsearch par mot-clé.

## Enchères publiques

- `POST /public/auctions` : créer une enchère.
- `DELETE /public/auctions/{auctionId}` : supprimer une enchère.
- `GET /public/auctions/{auctionId}` : récupérer une enchère.
- `GET /public/auctions` : lister les enchères (pagination possible).
- `GET /public/auctions/{auctionId}/products` : lister les produits d'une enchère.
- `GET /public/auctions/{auctionId}/bids` : lister les offres.
- `POST /public/auctions/{auctionId}/bids` : placer une offre.
- `POST /public/auctions/{auctionId}/products` : ajouter un produit à une enchère.
- `GET /public/auctions/{auctionId}/winning-product` : calculer le produit gagnant.

## Gestion des membres d'organisation

- `POST /api/orgmembers/{orgId}/members` : ajouter un membre.
- `PUT /api/orgmembers/{orgId}/members` : mettre à jour un rôle de membre.
- `GET /api/orgmembers/{orgId}/members/admins` : lister les admins.
- `GET /api/orgmembers/{orgId}/members` : lister les membres.
- `GET /api/orgmembers/{userId}/community` : récupérer la communauté d'un utilisateur.
- `GET /api/orgmembers/{userId}/group` : récupérer le groupe d'un utilisateur.
- `PATCH /api/orgmembers/{orgId}/members/{userId}/type` : modifier le type de membre.
- `POST /api/orgmembers/{orgId}/invite` : générer un lien d'invitation.
- `GET /api/orgmembers/{orgId}/invite/qr` : générer un QR code d'invitation.
- `POST /api/orgmembers/join?token={token}` : rejoindre une organisation via invitation.
- `PUT /api/orgmembers/{organisationId}/approve?userId={userId}` : approuver une demande d'adhésion.
- `POST /api/orgmembers/accept-invitation?token={token}` : accepter une invitation.
- `POST /api/orgmembers/memberships/{membershipId}/reject` : rejeter une demande d'adhésion.

## Besoins utilisateurs

- `POST /public/needs` : créer un besoin utilisateur.
- `POST /public/needs/toorg` : créer un besoin pour des organisations.
- `POST /public/needs/with-organisations` : créer un besoin et l’attacher à plusieurs organisations.
- `GET /public/needs/user/{userId}` : lister les besoins d’un utilisateur.
- `DELETE /public/needs/{id}` : supprimer un besoin.
- `GET /public/needs/user/{userId}/matching-products` : obtenir les produits correspondants.

## Conseils frontend

- Pour un frontend sans webhook direct, utilisez `GET /api/organisations/{orgId}/escrows` pour faire du polling et récupérer l'état des escrows.
- Pour les notifications en temps réel, inscrivez un webhook via `/api/organisations/{orgId}/webhooks` et recevez les mises à jour de l’état d’un escrow, de la validation produit ou des réclamations.
- Utilisez `transactionRef`, `escrowId` et `productId` pour assurer l'idempotence côté frontend.
- Vérifiez toujours le payload et le code de statut HTTP pour afficher des erreurs utiles à l'utilisateur.

## Test

```bash
./mvnw test
```

## Documentation API

Si OpenAPI / Swagger est disponible, utilisez-la pour explorer les schémas de requêtes et les DTO.

# Contributions

Les contributions sont les bienvenues. Ouvrez une Issue ou soumettez une Pull Request si vous souhaitez ajouter des routes ou améliorer la documentation.
