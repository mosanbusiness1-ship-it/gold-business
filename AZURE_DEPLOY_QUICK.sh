#!/bin/bash
# 🚀 QUICK-START: Commandes Déploiement Azure App Services
# Script pour déployer gold-business sur Azure
# Usage: bash AZURE_DEPLOY_QUICK.sh

set -e

# ═══════════════════════════════════════════════════════════════
# CONFIGURATION À MODIFIER
# ═══════════════════════════════════════════════════════════════

RESOURCE_GROUP="rg-gold-business"
APP_SERVICE_NAME="gold-business-app"
APP_PLAN_NAME="plan-gold-business"
REGION="eastus"
SKU="B2"
JAVA_VERSION="17"

# Variables d'environnement (À REMPLIR!)
SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-}"
SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-}"
SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-}"
REDIS_URL="${REDIS_URL:-}"
KAFKA_BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-}"
ASTRA_DB_API_ENDPOINT="${ASTRA_DB_API_ENDPOINT:-}"
ASTRA_DB_APPLICATION_TOKEN="${ASTRA_DB_APPLICATION_TOKEN:-}"

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 1: VÉRIFICATIONS PRÉREQUIS
# ═══════════════════════════════════════════════════════════════

check_prerequisites() {
    echo "📋 Vérification des prérequis..."
    
    # Vérifier Java
    if ! command -v java &> /dev/null; then
        echo "❌ Java n'est pas installé"
        exit 1
    fi
    echo "✅ Java: $(java -version 2>&1 | head -1)"
    
    # Vérifier Maven
    if ! command -v mvn &> /dev/null; then
        echo "❌ Maven n'est pas installé"
        exit 1
    fi
    echo "✅ Maven: $(mvn -version | head -1)"
    
    # Vérifier Azure CLI
    if ! command -v az &> /dev/null; then
        echo "❌ Azure CLI n'est pas installé"
        echo "   Télécharger: https://learn.microsoft.com/en-us/cli/azure/install-azure-cli"
        exit 1
    fi
    echo "✅ Azure CLI: $(az --version | head -1)"
    
    # Vérifier authentification Azure
    if ! az account show &> /dev/null; then
        echo "❌ Non authentifié à Azure"
        echo "   Exécutez: az login"
        exit 1
    fi
    echo "✅ Authentifié à Azure: $(az account show --query 'name' -o tsv)"
}

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 2: BUILD
# ═══════════════════════════════════════════════════════════════

build_project() {
    echo ""
    echo "🔨 Building du projet..."
    cd "$(dirname "$0")"
    mvn clean package -DskipTests -q
    echo "✅ Build réussi"
    ls -lh target/gold-business-0.0.1-SNAPSHOT.jar
}

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 3: CRÉATION AZURE
# ═══════════════════════════════════════════════════════════════

create_resource_group() {
    echo ""
    echo "📦 Création du groupe de ressources..."
    
    if az group exists --name "$RESOURCE_GROUP" --query value -o tsv | grep -q "true"; then
        echo "✅ Le groupe de ressources '$RESOURCE_GROUP' existe déjà"
    else
        az group create \
            --name "$RESOURCE_GROUP" \
            --location "$REGION"
        echo "✅ Groupe de ressources créé"
    fi
}

create_app_plan() {
    echo ""
    echo "📋 Création du plan App Service (SKU: $SKU)..."
    
    if az appservice plan show --name "$APP_PLAN_NAME" --resource-group "$RESOURCE_GROUP" &> /dev/null; then
        echo "✅ Le plan '$APP_PLAN_NAME' existe déjà"
    else
        az appservice plan create \
            --name "$APP_PLAN_NAME" \
            --resource-group "$RESOURCE_GROUP" \
            --sku "$SKU" \
            --is-linux \
            -q
        echo "✅ Plan App Service créé"
    fi
}

create_app_service() {
    echo ""
    echo "🌐 Création de l'App Service..."
    
    if az webapp show --name "$APP_SERVICE_NAME" --resource-group "$RESOURCE_GROUP" &> /dev/null; then
        echo "✅ L'App Service '$APP_SERVICE_NAME' existe déjà"
    else
        az webapp create \
            --name "$APP_SERVICE_NAME" \
            --resource-group "$RESOURCE_GROUP" \
            --plan "$APP_PLAN_NAME" \
            --runtime "JAVA|${JAVA_VERSION}-java${JAVA_VERSION}" \
            -q
        echo "✅ App Service créé"
        echo "   URL: https://${APP_SERVICE_NAME}.azurewebsites.net"
    fi
}

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 4: CONFIGURATION DES VARIABLES D'ENVIRONNEMENT
# ═══════════════════════════════════════════════════════════════

configure_app_settings() {
    echo ""
    echo "⚙️  Configuration des variables d'environnement..."
    
    # Tableau des settings
    declare -A settings=(
        ["SPRING_PROFILES_ACTIVE"]="azure"
        ["SPRING_DATASOURCE_URL"]="$SPRING_DATASOURCE_URL"
        ["SPRING_DATASOURCE_USERNAME"]="$SPRING_DATASOURCE_USERNAME"
        ["SPRING_DATASOURCE_PASSWORD"]="$SPRING_DATASOURCE_PASSWORD"
        ["REDIS_URL"]="$REDIS_URL"
        ["KAFKA_BOOTSTRAP_SERVERS"]="$KAFKA_BOOTSTRAP_SERVERS"
        ["ASTRA_DB_API_ENDPOINT"]="$ASTRA_DB_API_ENDPOINT"
        ["ASTRA_DB_APPLICATION_TOKEN"]="$ASTRA_DB_APPLICATION_TOKEN"
    )
    
    # Construire les commandes de settings
    local settings_args=()
    for key in "${!settings[@]}"; do
        value="${settings[$key]}"
        if [ -n "$value" ]; then
            settings_args+=("${key}=${value}")
        else
            echo "⚠️  Variable vide: $key"
        fi
    done
    
    if [ ${#settings_args[@]} -gt 0 ]; then
        az webapp config appsettings set \
            --resource-group "$RESOURCE_GROUP" \
            --name "$APP_SERVICE_NAME" \
            --settings "${settings_args[@]}" \
            -q
        echo "✅ Variables d'environnement configurées"
    else
        echo "❌ Aucune variable d'environnement configurée"
        echo "   Veuillez définir les variables d'environnement avant le déploiement"
    fi
}

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 5: DÉPLOIEMENT
# ═══════════════════════════════════════════════════════════════

deploy() {
    echo ""
    echo "🚀 Déploiement sur Azure..."
    
    JAR_PATH="target/gold-business-0.0.1-SNAPSHOT.jar"
    
    if [ ! -f "$JAR_PATH" ]; then
        echo "❌ Le JAR n'existe pas: $JAR_PATH"
        exit 1
    fi
    
    # Créer un ZIP contenant le JAR et web.config
    echo "   Préparation du package..."
    cd "$(dirname "$0")/target"
    
    # Créer web.config pour lancer le JAR
    cat > web.config << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <system.webServer>
    <handlers>
      <add name="httpPlatformHandler" path="*" verb="*" modules="httpPlatformHandler" resourceType="Unspecified" />
    </handlers>
    <httpPlatform processPath="%JAVA_HOME%\bin\java.exe"
                  arguments="-Dserver.port=%HTTP_PLATFORM_PORT% -jar gold-business-0.0.1-SNAPSHOT.jar"
                  stdoutLogEnabled="true"
                  stdoutLogFile="..\logs\stdout"
                  startupTimeLimit="60"
                  requestTimeout="00:05:00" />
  </system.webServer>
</configuration>
EOF
    
    # Créer le ZIP
    zip -q deploy.zip gold-business-0.0.1-SNAPSHOT.jar web.config
    
    # Déployer
    cd - > /dev/null
    az webapp deployment source config-zip \
        --resource-group "$RESOURCE_GROUP" \
        --name "$APP_SERVICE_NAME" \
        --src "target/deploy.zip" \
        -q
    
    echo "✅ Déploiement réussi!"
}

# ═══════════════════════════════════════════════════════════════
# ÉTAPE 6: VÉRIFICATION
# ═══════════════════════════════════════════════════════════════

verify_deployment() {
    echo ""
    echo "🔍 Vérification du déploiement..."
    echo ""
    
    APP_URL="https://${APP_SERVICE_NAME}.azurewebsites.net"
    
    echo "   Attendez 30 secondes pour le démarrage de l'application..."
    sleep 30
    
    echo "   Test de l'endpoint de santé..."
    if curl -s -o /dev/null -w "%{http_code}" "$APP_URL/api/v1/health" | grep -q "200"; then
        echo "✅ Application accessible!"
        echo ""
        echo "   URL: $APP_URL"
        echo "   Logs: az webapp log tail --resource-group $RESOURCE_GROUP --name $APP_SERVICE_NAME"
    else
        echo "⚠️  L'application n'est pas encore accessible"
        echo "   Elle peut prendre 1-2 minutes pour démarrer"
        echo "   Consultez les logs:"
        echo "   az webapp log tail --resource-group $RESOURCE_GROUP --name $APP_SERVICE_NAME"
    fi
}

# ═══════════════════════════════════════════════════════════════
# COMMANDES UTILES
# ═══════════════════════════════════════════════════════════════

show_usage() {
    cat << EOF
┌─────────────────────────────────────────────────────────────────┐
│        🚀 GOLD BUSINESS - Azure App Services Deployment         │
└─────────────────────────────────────────────────────────────────┘

📝 COMMANDES UTILES:

1. CONSULTER LES LOGS EN TEMPS RÉEL:
   az webapp log tail --resource-group $RESOURCE_GROUP --name $APP_SERVICE_NAME

2. REDÉPLOYER APRÈS UN CHANGEMENT:
   mvn clean package -DskipTests
   az webapp deployment source config-zip \
     --resource-group $RESOURCE_GROUP \
     --name $APP_SERVICE_NAME \
     --src target/deploy.zip

3. SUPPRIMER LES RESSOURCES (ATTENTION!):
   az group delete --resource-group $RESOURCE_GROUP --yes --no-wait

4. VOIR LES CONFIGURATIONS:
   az webapp config appsettings list \
     --resource-group $RESOURCE_GROUP \
     --name $APP_SERVICE_NAME

5. ACCÉDER À L'APPLICATION:
   https://${APP_SERVICE_NAME}.azurewebsites.net

6. VÉRIFIER LA SANTÉ DES SERVICES:
   curl https://${APP_SERVICE_NAME}.azurewebsites.net/api/v1/health/services

───────────────────────────────────────────────────────────────────
DOCUMENTATION COMPLÈTE: AZURE_APP_SERVICES_DEPLOYMENT.md
───────────────────────────────────────────────────────────────────
EOF
}

# ═══════════════════════════════════════════════════════════════
# MENU PRINCIPAL
# ═══════════════════════════════════════════════════════════════

main() {
    clear
    echo "┌─────────────────────────────────────────────────────────────────┐"
    echo "│        🚀 GOLD BUSINESS - Azure App Services Setup              │"
    echo "└─────────────────────────────────────────────────────────────────┘"
    echo ""
    
    if [ "$1" == "--help" ] || [ "$1" == "-h" ]; then
        show_usage
        exit 0
    fi
    
    # Étape 1: Vérifications
    check_prerequisites
    
    # Étape 2: Build
    build_project
    
    # Étape 3: Création Azure
    create_resource_group
    create_app_plan
    create_app_service
    
    # Étape 4: Configuration
    configure_app_settings
    
    # Étape 5: Déploiement
    deploy
    
    # Étape 6: Vérification
    verify_deployment
    
    # Aide
    show_usage
    
    echo ""
    echo "✅ Déploiement terminé!"
    echo ""
}

# Lancer si exécuté directement
if [ "${BASH_SOURCE[0]}" == "${0}" ]; then
    main "$@"
fi
