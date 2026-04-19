#  Voom – Notification Service

> Microservice de gestion des notifications de la plateforme **Voom**  
> Application de covoiturage

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?logo=springboot)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-KRaft-black?logo=apachekafka)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)

---

##  Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture](#-architecture)
- [Prérequis](#-prérequis)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [API REST](#-api-rest)
- [Événements Kafka](#-événements-kafka)
- [Tests](#-tests)
- [Structure du projet](#-structure-du-projet)
- [Choix techniques](#-choix-techniques)
- [Auteur](#-auteur)

---

##  Vue d'ensemble

Le **Notification Service** est un microservice indépendant de la plateforme Voom.  
Il fait partie d'une architecture microservices et communique avec les autres services via **Apache Kafka**.

### Responsabilités

| Responsabilité | Description |
|---|---|
|  **Réception d'événements** | Consomme les topics Kafka publiés par les autres microservices |
|  **Persistance** | Stocke toutes les notifications en base MySQL |
|  **API REST** | Expose les endpoints de consultation et de gestion |
|  **Statistiques** | Comptage des notifications lues / non lues par utilisateur |

### Types de notifications gérés

```
AFFECTATION_PROPOSEE / ACCEPTEE / REFUSEE
TRAJET_DEMARRE / TERMINE / ANNULE
DEMANDE_PRISE_EN_CHARGE / ANNULEE
INVITATION_TRAJET_RECUE / ACCEPTEE / REFUSEE
DEMANDE_AMITIE_RECUE / ACCEPTEE
AVIS_RECU
SIGNALEMENT_TRAITE
DOCUMENT_VERIFIE / REJETE
ACHAT_TOKEN_CONFIRME / SOLDE_TOKEN_FAIBLE / CREDITS_DEBITES
SYSTEME
```

---

##  Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Plateforme Voom                         │
│                                                             │
│  ┌──────────────┐  ┌─────────────┐  ┌──────────────────┐  │
│  │Trajet-Service│  │ User-Service│  │Paiement-Service  │  │
│  └──────┬───────┘  └──────┬──────┘  └────────┬─────────┘  │
│         └─────────────────┴─────────────────┘             │
│                            │  Publish events               │
│                     ┌──────▼──────┐                        │
│                     │    KAFKA    │  Topics: voom.*.events │
│                     └──────┬──────┘                        │
│                            │  Consume                      │
│                ┌───────────▼──────────┐                    │
│                │  NOTIFICATION SERVICE│  :8083             │
│                │  ┌────────────────┐  │                    │
│                │  │   REST API     │◄─┼── App Mobile       │
│                │  └───────┬────────┘  │                    │
│                │  ┌───────▼────────┐  │                    │
│                │  │   MySQL DB     │  │                    │
│                │  └────────────────┘  │                    │
│                └──────────────────────┘                    │
└─────────────────────────────────────────────────────────────┘
```

### Flux de données

```
1. Un événement métier se produit (ex: affectation créée)
         ↓
2. L'autre microservice publie sur Kafka
         ↓
3. NotificationEventConsumer reçoit l'événement
         ↓
4. NotificationService crée et persiste la notification
         ↓
5. L'app mobile consulte GET /api/v1/notifications/utilisateur/{id}
```

---

##  Prérequis

| Outil | Version minimale |
|---|---|
| Java JDK | 21+ |
| Maven | 3.8+ |
| MySQL Server | 8.0+ |
| Docker & Docker Compose | 20+ |
| IntelliJ IDEA | 2023+ (recommandé) |

---

##  Installation

### Étape 1 — Cloner le projet

```bash
git clone https://github.com/<votre-username>/notification-service.git
cd notification-service
```

### Étape 2 — Démarrer Kafka avec Docker

```bash
docker-compose up -d
```

Vérifier que les conteneurs tournent :

```bash
docker-compose ps
```

Résultat attendu :
```
voom-kafka      Up
voom-kafka-ui   Up
```

### Étape 3 — Configurer la base de données

Ouvrir `src/main/resources/application.yml` et mettre à jour :

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/voom_notifications?createDatabaseIfNotExist=true
    username: root
    password: MotDePasse 
```

>  La base de données `voom_notifications` et la table `notifications`
> sont créées **automatiquement** au premier démarrage.

### Étape 4 — Lancer l'application

Depuis IntelliJ :
```
Run → NotificationServiceApplication
```

Ou via Maven :
```bash
mvn spring-boot:run
```

### Étape 5 — Vérifier le démarrage

```
http://localhost:8083/actuator/health
```

Réponse attendue :
```json
{
  "status": "UP"
}
```

---

##  Configuration

Toute la configuration se trouve dans `src/main/resources/application.yml`.

| Variable | Valeur par défaut | Description |
|---|---|---|
| `server.port` | `8083` | Port du service |
| `datasource.username` | `root` | Utilisateur MySQL |
| `datasource.password` | *(vide)* | Mot de passe MySQL |
| `kafka.bootstrap-servers` | `localhost:9092` | Adresse du broker Kafka |
| `kafka.consumer.group-id` | `notification-service-group` | Groupe Kafka |

---

##  API REST

**Base URL :** `http://localhost:8083/api/v1/notifications`

### Endpoints disponibles

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Créer une notification |
| `GET` | `/utilisateur/{id}` | Toutes les notifications paginées |
| `GET` | `/utilisateur/{id}/non-lues` | Notifications non lues |
| `GET` | `/utilisateur/{id}/type/{type}` | Filtrer par type |
| `GET` | `/{id}` | Une notification par ID |
| `PATCH` | `/{id}/lue` | Marquer une notification comme lue |
| `PATCH` | `/utilisateur/{id}/tout-lire` | Tout marquer comme lu |
| `GET` | `/utilisateur/{id}/stats` | Statistiques |
| `DELETE` | `/utilisateur/{id}/lues` | Supprimer les notifications lues |

---

### Créer une notification

**Requête :**
```http
POST http://localhost:8083/api/v1/notifications
Content-Type: application/json

{
  "destinataireId": "user-123",
  "type": "AFFECTATION_PROPOSEE",
  "titre": "Nouvelle affectation disponible",
  "message": "Un trajet Dakar-Thiès vous a été proposé pour demain 08h00",
  "lienAction": "/affectations/aff-1",
  "sourceId": "aff-1"
}
```

**Réponse (201) :**
```json
{
  "id": "a1b2c3d4-...",
  "destinataireId": "user-123",
  "type": "AFFECTATION_PROPOSEE",
  "titre": "Nouvelle affectation disponible",
  "message": "Un trajet Dakar-Thiès vous a été proposé pour demain 08h00",
  "dateCreation": "2026-04-19T22:00:00",
  "lue": false,
  "lienAction": "/affectations/aff-1",
  "sourceId": "aff-1",
  "dateLecture": null
}
```

---

### Lister les notifications

**Requête :**
```http
GET http://localhost:8083/api/v1/notifications/utilisateur/user-123?page=0&taille=20
```

**Réponse (200) :**
```json
{
  "notifications": [
    {
      "id": "a1b2c3d4-...",
      "destinataireId": "user-123",
      "type": "AFFECTATION_PROPOSEE",
      "titre": "Nouvelle affectation disponible",
      "lue": false,
      "dateCreation": "2026-04-19T22:00:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 20,
  "dernierePage": true
}
```

---

### Statistiques

**Requête :**
```http
GET http://localhost:8083/api/v1/notifications/utilisateur/user-123/stats
```

**Réponse (200) :**
```json
{
  "utilisateurId": "user-123",
  "totalNotifications": 10,
  "notificationsNonLues": 3,
  "notificationsLues": 7
}
```

---

### Codes de réponse

| Code | Signification |
|---|---|
| `200 OK` | Succès |
| `201 Created` | Notification créée |
| `204 No Content` | Opération réussie sans corps |
| `400 Bad Request` | Données invalides |
| `404 Not Found` | Notification introuvable |
| `500 Internal Server Error` | Erreur serveur |

---

##  Événements Kafka

Ce service **consomme** les topics suivants publiés par les autres microservices :

| Topic Kafka | Événements attendus |
|---|---|
| `voom.trajets.events` | `TRAJET_DEMARRE`, `TRAJET_TERMINE`, `TRAJET_ANNULE` |
| `voom.affectations.events` | `AFFECTATION_PROPOSEE`, `AFFECTATION_ACCEPTEE`, `AFFECTATION_REFUSEE` |
| `voom.invitations.events` | `INVITATION_TRAJET_RECUE`, `INVITATION_ACCEPTEE`, `INVITATION_REFUSEE` |
| `voom.amis.events` | `DEMANDE_AMITIE_RECUE`, `DEMANDE_AMITIE_ACCEPTEE` |
| `voom.avis.events` | `AVIS_RECU` |
| `voom.documents.events` | `DOCUMENT_VERIFIE`, `DOCUMENT_REJETE` |
| `voom.paiements.events` | `ACHAT_TOKEN_CONFIRME`, `SOLDE_TOKEN_FAIBLE`, `CREDITS_DEBITES` |

### Format du message Kafka

```json
{
  "destinataireId": "user-123",
  "type": "AFFECTATION_PROPOSEE",
  "titre": "Nouvelle affectation disponible",
  "message": "Un trajet vous a été proposé pour demain à 08h00.",
  "lienAction": "/affectations/aff-456",
  "sourceId": "aff-456"
}
```

### Tester avec Kafka UI

1. Ouvrir `http://localhost:8090`
2. Aller dans **Topics → voom.affectations.events**
3. Cliquer **Produce Message**
4. Coller le JSON ci-dessus dans le champ **Value**
5. Cliquer **Send**
6. Vérifier : `GET /api/v1/notifications/utilisateur/user-123`

---

##  Tests

### Fichier de tests HTTP

Le fichier `api-tests.http` contient tous les tests prêts à l'emploi.  
Ouvrir dans IntelliJ et cliquer sur ▶️ pour exécuter chaque requête.

### Lancer les tests unitaires

```bash
mvn test
```

---

##  Structure du projet

```
notification-service/
│
├── src/
│   └── main/
│       ├── java/com/voom/notification/
│       │   ├── NotificationServiceApplication.java
│       │   ├── config/
│       │   │   └── AsyncConfig.java
│       │   ├── controller/
│       │   │   └── NotificationController.java
│       │   ├── dto/
│       │   │   ├── CreateNotificationDTO.java
│       │   │   ├── NotificationResponseDTO.java
│       │   │   ├── NotificationPageDTO.java
│       │   │   └── NotificationStatsDTO.java
│       │   ├── event/
│       │   │   ├── NotificationEvent.java
│       │   │   └── NotificationEventConsumer.java
│       │   ├── exception/
│       │   │   ├── NotificationNotFoundException.java
│       │   │   ├── ErrorResponse.java
│       │   │   └── GlobalExceptionHandler.java
│       │   ├── model/
│       │   │   ├── Notification.java
│       │   │   └── TypeNotification.java
│       │   ├── repository/
│       │   │   └── NotificationRepository.java
│       │   └── service/
│       │       └── NotificationService.java
│       └── resources/
│           └── application.yml
│
├── docker-compose.yml
├── api-tests.http
├── pom.xml
└── README.md
```

---

##  Choix techniques

| Technologie | Justification |
|---|---|
| **Spring Boot 3.2.5** | Framework standard pour les microservices Java |
| **Apache Kafka (KRaft)** | Communication asynchrone et découplée entre microservices |
| **MySQL** | Base relationnelle robuste avec indexation efficace |
| **Lombok** | Réduction du boilerplate (getters, builders, logs) |
| **Spring Actuator** | Health checks et métriques pour le monitoring |
| **Docker Compose** | Démarrage simplifié de l'infrastructure (Kafka) |

---

## 👤 Auteur

**[Mouhamadou Aliou BA]** — Microservice Notifications  
