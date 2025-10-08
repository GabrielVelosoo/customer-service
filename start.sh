#!/bin/sh
echo "Waiting for Keycloak to be ready..."

# Waiting Keycloak
until curl -s http://keycloak:8080/realms/ecommerce >/dev/null 2>&1; do
  echo "Keycloak is not ready yet. Retrying in 3 seconds..."
  sleep 3
done

echo "Keycloak ready! Starting application..."
exec java -jar /app/app.jar
