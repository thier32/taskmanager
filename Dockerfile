# ==========================================
# ÉTAPE 1 : Construction (Build) du projet
# ==========================================
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# 1. Copier les fichiers de configuration de la racine du projet
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 2. Copie des fichiers build.gradle de chaque sous-module 
# (nécessaire pour que Gradle comprenne la structure avant de télécharger les dépendances)
COPY java-frame-base/build.gradle ./java-frame-base/
COPY taskmanager-application/build.gradle ./taskmanager-application/
COPY taskmanager-management/build.gradle ./taskmanager-management/
COPY taskmanager-users/build.gradle ./taskmanager-users/
# (Ajoutez autant de lignes COPY que vous avez de sous-modules)

# Télécharger les dépendances pour tous les modules (mise en cache)
RUN ./gradlew dependencies --no-daemon

# 3. Copier tout le reste du code source
COPY . .

# 4. Compiler et empaqueter uniquement le module exécutable (ex: app-api ou application)
# Remplacez ':app-api:bootJar' par le chemin exact de votre module Spring Boot principal
RUN ./gradlew :taskmanager-application:bootJar -x test --no-daemon

# ==========================================
# ÉTAPE 2 : Exécution en production (Runtime)
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le fichier .jar généré spécifiquement dans le sous-module de build
# Adaptez 'app-api' par le nom de votre module principal
COPY --from=build /app/taskmanager-application/build/libs/*.jar app.jar

# Exposer le port de Spring Boot
EXPOSE 8091

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]