#!/bin/sh

if [ -f "$SPRING_DATASOURCE_PASSWORD_FILE" ]; then
    export SPRING_DATASOURCE_PASSWORD=$(cat "$SPRING_DATASOURCE_PASSWORD_FILE")
fi

if [ -f "$APP_AES_KEY_FILE" ]; then
    export APP_AES_KEY=$(cat "$APP_AES_KEY_FILE")
fi

if [ -f "$SPRING_DATASOURCE_USERNAME_FILE" ]; then
    export SPRING_DATASOURCE_USERNAME=$(cat "$SPRING_DATASOURCE_USERNAME_FILE")
fi

echo "✅ Secrets cargados, iniciando aplicación..."
exec java -jar app.jar
