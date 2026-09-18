# Documentation technique

## 1. Objet du projet

Task Manager est une API REST Java/Spring Boot permettant de :

- créer et administrer des utilisateurs ;
- authentifier les utilisateurs avec un jeton JWT ;
- créer, modifier, rechercher et supprimer des tâches ;
- filtrer les résultats avec pagination et critères génériques.

L'application est un monolithe modulaire : les modules sont compilés séparément, mais un seul module est exécutable.

## 2. Architecture

```text
taskmanager-application
        |
taskmanager-management
        | \
        |  taskmanager-users
        |
  java-frame-base
```

### Modules

| Module | Responsabilité | Dépend de |
| --- | --- | --- |
| `java-frame-base` | Socle commun : contrôleurs CRUD, services, repositories, filtres, pagination et réponses | Spring Data JPA, Web |
| `taskmanager-users` | Utilisateurs, rôles, inscription, connexion, JWT et configuration Spring Security | `java-frame-base` |
| `taskmanager-management` | Domaine des tâches, DTO, business, repository et contrôleur REST | `java-frame-base`, `taskmanager-users` |
| `taskmanager-application` | Point d'entrée Spring Boot et configuration d'exécution | `taskmanager-management` |

Le point d'entrée est `com.cova.taskmanager.TaskManagerApplication`.

### Flux applicatif

```text
HTTP
  -> Controller
  -> Business
  -> Service / Repository
  -> JPA / base de données
```

Les contrôleurs d'utilisateurs et de tâches réutilisent `BaseController`, qui fournit les opérations CRUD génériques. `TaskController` ajoute la récupération des statuts de tâche.

## 3. Technologies et prérequis

- Java 21 ;
- Gradle Wrapper ;
- Spring Boot 3.5.15 ;
- Spring Web ;
- Spring Data JPA / Hibernate ;
- Spring Security ;
- JJWT 0.12.6 ;
- Springdoc OpenAPI 2.8.16 ;
- MySQL en configuration applicative principale ;
- H2 disponible pour les modules et les scénarios locaux.

## 4. Installation et exécution

### Compilation et tests

Depuis la racine :

```powershell
.\gradlew test
.\gradlew :taskmanager-application:build
```

Pour exécuter uniquement les tests du module applicatif :

```powershell
.\gradlew :taskmanager-application:test
```

Les tests actuels vérifient principalement le chargement du contexte Spring. Il n'existe pas encore de couverture fonctionnelle complète des routes REST, de la sécurité ou des accès aux données.

### Exécution locale

```powershell
.\gradlew :taskmanager-application:bootRun
```

Le port par défaut est `8091`. La configuration de l'application utilise MySQL à l'adresse `host.docker.internal:3306`, avec la base `task` et l'utilisateur `root`.

Une base MySQL doit donc être accessible avant le démarrage. Pour une exécution sans MySQL, il faut activer et adapter la configuration H2 commentée dans `taskmanager-application/src/main/resources/application.properties`.

### Exécution Docker

Le projet fournit un build Docker multi-stage : Gradle/JDK 21 construit l'application, puis une image Eclipse Temurin JRE 21 exécute le JAR final. Docker Desktop doit être démarré avant les commandes suivantes.

#### Fonctionnement du Dockerfile

Le `Dockerfile` est organisé en deux étapes :

1. **Construction (`build`)**
  - utilise l'image `gradle:8.5-jdk21` ;
  - copie les fichiers Gradle et les fichiers `build.gradle` des quatre modules ;
  - télécharge les dépendances avec `./gradlew dependencies --no-daemon` afin de profiter du cache Docker ;
  - copie ensuite le code source ;
  - génère uniquement le JAR exécutable avec `:taskmanager-application:bootJar`.

  Les tests sont volontairement ignorés pendant la construction de l'image avec l'option `-x test`. Ils doivent être exécutés séparément avant le build Docker avec `./gradlew test`.

2. **Exécution (`runtime`)**
  - utilise une image `eclipse-temurin:21-jre-alpine`, sans JDK ni outils Gradle ;
  - copie le JAR produit vers `/app/app.jar` ;
  - expose le port `8091` ;
  - démarre Spring Boot avec `java -jar app.jar`.

Cette séparation réduit la taille de l'image finale et évite d'y embarquer le code source, Gradle et le JDK. Le fichier `Dockerfile` construit uniquement l'application : la base MySQL reste un service externe.

Construire l'image depuis la racine du projet :

```powershell
docker build -t taskmanager .
```

Démarrer l'API :

```powershell
docker run --rm -p 6001:8091 taskmanager
```

L'API est alors disponible sur `http://localhost:6001` `http://127.0.0.1:6001`. Le port `8091` du conteneur est publié sur le port 6001 de la machine hôte.

L'API doc est alors disponible sur `http://localhost:6001/swagger-ui/index.html` `http://127.0.0.1:6001/swagger-ui/index.html`.

Le conteneur utilise la configuration MySQL de l'application (`host.docker.internal:3306`, base `task`). MySQL doit donc être démarré et accessible depuis Docker avant le lancement de l'API. Le conteneur Task Manager ne lance pas lui-même MySQL et ne persiste pas de données localement.

Les propriétés Spring peuvent être surchargées au lancement du conteneur :

```powershell
docker run --rm -p 8091:8091 `
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/task `
  -e SPRING_DATASOURCE_USERNAME=root `
  -e SPRING_DATASOURCE_PASSWORD=mot_de_passe `
  -e APP_JWT_SECRET=secret_de_production taskmanager
```

Pour arrêter le conteneur, utilisez `Ctrl+C` ou lancez-le en arrière-plan avec `-d`, puis arrêtez-le avec `docker stop <container_id>`.

### Documentation OpenAPI

Une fois l'application démarrée :

- Swagger UI : `http://localhost:8091/swagger-ui/index.html` ;
- spécification OpenAPI : `http://localhost:8091/v3/api-docs` ;
- console H2 : `http://localhost:8091/h2-console` lorsqu'elle est activée et accessible.

## 5. Configuration

La configuration principale est dans `taskmanager-application/src/main/resources/application.properties`.

| Propriété | Valeur par défaut | Description |
| --- | --- | --- |
| `server.port` | `8091` | Port HTTP |
| `spring.datasource.url` | `jdbc:mysql://host.docker.internal:3306/task` | URL JDBC principale |
| `spring.datasource.username` | `root` | Utilisateur de la base |
| `spring.datasource.password` | vide | Mot de passe de la base |
| `spring.jpa.hibernate.ddl-auto` | `update` | Mise à jour automatique du schéma |
| `app.jwt.secret` | défini dans le fichier | Secret de signature JWT |
| `app.jwt.expiration-ms` | `86400000` | Durée du JWT, 24 heures |
| `app.cors.allowed-origins` | `*` dans l'application | Origines CORS autorisées |

Les propriétés Spring peuvent être surchargées par les variables d'environnement correspondantes, par exemple `SERVER_PORT`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` et `SPRING_DATASOURCE_PASSWORD`. Le secret JWT doit être fourni par une configuration externe en environnement de production.

Les modules `taskmanager-users` et `taskmanager-management` contiennent également une configuration H2 fichier dans `~/.taskmanagger/data1/taskmanager_bd`. La configuration effectivement utilisée dépend du module démarré et de la fusion des ressources dans l'application finale.

## 6. API REST

### Format de réponse

Les opérations métier utilisent généralement `ResponseDto` :

```json
{
  "code": "200",
  "message": "Success",
  "result": {}
}
```

Les listes sont paginées avec une structure de type `PageResponse` :

```json
{
  "rows": [],
  "total": 0,
  "totalPages": 0,
  "size": 50,
  "page": 0
}
```

Les critères de recherche sont transmis dans le corps JSON d'un `GET` lorsque nécessaire. Les clés usuelles sont `page`, `size`, `sortBy` et `sortDir`.

### Authentification

| Méthode | Route | Authentification | Corps principal |
| --- | --- | --- | --- |
| `POST` | `/register` | publique | `username`, `password`, `confirmPassword`, `email`, `name`, `roles` |
| `POST` | `/login` | publique | `username`, `password` |

La réponse de connexion contient notamment le nom d'utilisateur, l'e-mail, les rôles et le jeton Bearer. Les appels protégés doivent envoyer :

```http
Authorization: Bearer <jwt>
```

### Utilisateurs

| Méthode | Route | Corps / paramètres | Résultat |
| --- | --- | --- | --- |
| `GET` | `/users/` | critères JSON optionnels | page de `UserResultDto` |
| `POST` | `/users/` | `UserDto` | `UserResultDto` |
| `PUT` | `/users/{id}` | `UserDto` | `UserResultDto` |
| `DELETE` | `/users/{id}` | critères JSON optionnels | identifiant ou résultat de suppression |

Les réponses utilisateur exposent notamment `userId`, `name`, `email` et `status`. Ces routes nécessitent un JWT valide.

### Tâches

| Méthode | Route | Corps / paramètres | Résultat |
| --- | --- | --- | --- |
| `GET` | `/api/tasks` | critères JSON optionnels | page de `TaskResultDto` |
| `POST` | `/api/tasks` | `title`, `description`, `status` | `TaskResultDto` |
| `PUT` | `/api/tasks/{id}` | `title`, `description`, `status` | `TaskResultDto` |
| `DELETE` | `/api/tasks/{id}` | critères JSON optionnels | identifiant ou résultat de suppression |
| `GET` | `/api/tasks/status` | aucun | liste des statuts |

Toutes les routes de tâches nécessitent un JWT. Les tâches sont associées à l'utilisateur courant et les recherches sont filtrées par cet utilisateur.

## 7. Modèle métier

### Utilisateur

L'entité `User` représente un compte applicatif et implémente `UserDetails`. Elle contient notamment :

- `userId` ;
- `username` ;
- `name` ;
- `email` ;
- `password` encodé ;
- `roles` ;
- `status`.

La connexion peut rechercher le compte par nom d'utilisateur ou par adresse e-mail.

### Tâche

L'entité `Task` est persistée dans la table `tasks` et contient notamment :

- `taskId` ;
- `name` et `title` ;
- `description` ;
- `status` ;
- une relation parent/enfants ;
- les informations d'audit héritées de `BaseModel`.

Les statuts disponibles sont `STARTED`, `CREATED`, `IN_PROGRESS`, `CANCEL`, `CLOSED` et `TERMINATED`. Une valeur absente, vide ou inconnue est ramenée à `CREATED` selon la logique existante.

## 8. Sécurité

Le flux JWT est le suivant :

1. l'utilisateur s'inscrit et son mot de passe est encodé par Spring Security ;
2. `/login` vérifie le nom d'utilisateur ou l'e-mail et le mot de passe ;
3. l'application génère un JWT contenant le sujet et les autorités ;
4. `JWTAuthorizationFilter` lit l'en-tête `Authorization` ;
5. les autorités sont placées dans le `SecurityContext` ;
6. les ressources protégées sont accessibles si l'authentification est valide.

La chaîne API est stateless, désactive CSRF pour les API et protège par défaut les routes sous `/api/**`. Swagger, la console H2 et certaines routes d'authentification sont déclarées publiques dans la configuration de sécurité.

## 9. Autres Points techniques

- Le secret JWT est actuellement présent en clair dans les propriétés et doit être externalisé.
- La configuration principale utilise MySQL.
- La chaîne de sécurité whitelist `/auth/**` et `/api/auth/**`, tandis que les contrôleurs exposent `/login` et `/register`. Cette incohérence doit être corrigée ou documentée explicitement.
- CORS est désactivé dans la chaîne API alors que des beans et propriétés CORS existent ; le comportement doit être validé avec le client réel.
- Les tests ne couvrent pas encore les contrats HTTP, la sécurité JWT, les filtres, la pagination ni les repositories.
- Les DTO `TaskUpdateDto`, `TaskDeleteDto`, `UserUpdateDto` et `UserDeleteDto` existent mais ne sont pas utilisés par les contrôleurs actuels.
- `PUT /api/tasks/{id}` reçoit actuellement `TaskCreateDto`.

## 10. Structure de code de référence

```text
java-frame-base/src/main/java/com/frame/base
  business/       logique métier générique
  controller/     CRUD REST générique
  criteria/       filtres et spécifications JPA
  dto/            réponses et pagination
  repository/     repository de base
  services/       services communs

taskmanager-users/src/main/java/com/cova/taskmanager/users
  config/         JWT et Spring Security
  controller/     authentification et utilisateurs
  dto/user/       contrats utilisateur
  model/          User, Role et statuts
  services/       service utilisateur

taskmanager-management/src/main/java/com/cova/taskmanager
  business/       logique métier des tâches
  controller/     API des tâches
  dto/task/       contrats tâche
  model/          Task et statuts
  repository/     accès aux tâches
```

Pour ajouter une nouvelle ressource CRUD, le pattern existant consiste à définir le modèle, les DTO, le repository et le business, puis à faire hériter le contrôleur de `BaseController` lorsque les opérations génériques suffisent.
