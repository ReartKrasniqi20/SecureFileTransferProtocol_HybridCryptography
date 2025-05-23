# Anora Streaming Service

Anora is a modular, microservices-based video streaming platform built with scalability, personalization, and maintainability in mind. It is composed of four independent services that handle different aspects of the system.

---

## Microservices Architecture

###  1. User Service

Handles user management, authentication, and profile operations.

**Key Features:**
- JWT-based authentication
- Password reset & update functionality
- Kafka integration for user events

**Endpoints:**
- `POST /api/auth/register` – Register a new user
- `POST /api/auth/login` – Authenticate and retrieve JWT
- `GET /api/user/profile` – Get current user's profile
- `PUT /api/user/profile` – Update user profile
- `POST /api/password/forgot/reset/change` – Password management

---

###  2. Subscription Service

Manages subscription plans and integrates with Stripe for payments.

**Key Features:**
- Stripe Checkout sessions
- Webhook handling for payment success
- Kafka integration with user and tier events

**Endpoints:**
- `POST /api/subscriptions` – Create a subscription
- `GET /api/subscriptions` – List all subscriptions
- `GET /api/subscriptions/user/{userId}` – Subscriptions by user
- `PUT /api/subscriptions/{id}/cancel` – Cancel subscription
- `POST /api/subscriptions/checkout` – Create Stripe session
- `POST /webhook` – Stripe webhook endpoint

---

###  3. Media Service

Handles video metadata, streaming, and user content interaction.

**Key Features:**
- HLS video streaming
- Rating and review system
- Search and filtering for video content

**Endpoints:**
- `POST /continue-watching` – Saves or updates the playback progress of a video for the authenticated user.
- `GET /continue-watching` – Retrieves all continue-watching entries for the authenticated user.
- `POST /favorites` – Adds a video to the authenticated user's favorites list.
- `GET /favorites` – Retrieves the list of favorite videos for the authenticated user.
- `DELETE /favorites` – Removes a video from the authenticated user's favorites list.
- `POST /ratings` – Submits or updates a rating and optional review for a video by the authenticated user.
- `GET /ratings/{videoId}` – Retrieves all ratings and reviews for a specific video.
- `GET /ratings/my/{videoId}` – Retrieves the authenticated user's rating and review for a specific video.
- `GET /stream/{videoId}/{fileName}` – Streams a specific video file for a given video ID.
- `GET /videos` – Retrieves all available video metadata records.
- `GET /videos/{id}` – Retrieves metadata for a specific video by its ID.
- `GET /videos/search?q={keyword}` – Searches videos by a given keyword.
- `GET /videos/genre/{genre}` – Retrieves videos that belong to a specific genre.
- `GET /videos/tags/{tag}` – Retrieves videos associated with a specific tag.
- `GET /videos/content-rating/{rating}` – Retrieves videos based on content rating (e.g., PG, R).
- `GET /videos/recent` – Retrieves the most recently added videos.
- `POST /watch-history` – Saves a new watch history entry for the authenticated user.
- `GET /watch-history` – Retrieves the watch history for the authenticated user.

---

### 4. Profile Service

Manages user personalization: favorites, watch history, and continue-watching.

**Key Features:**
- MongoDB-based storage
- Favorite & history management
- Watch progress tracking

**Endpoints:**
- `GET /api/profiles` – Retrieves the profile information of the authenticated user.
- `GET /api/profiles/movies` – Retrieves the list of movies associated with the authenticated user.
- `GET /api/profiles/statistics` – Retrieves genre statistics (counts) for the authenticated user's watched movies, returned as a map where keys are genre 

---

##  Unit Testing

All services include dedicated unit tests using:
- `JUnit`
- `Mockito`
- `Spring Boot Test`

Tests cover service logic, controller behavior, and basic integration scenarios.

---

##  Technologies Used

- **Java + Spring Boot**
- **MongoDB** & **PostgreSQL**
- **Kafka** – for asynchronous events
- **Stripe API** – for payments
- **JWT** – for secure auth
- **Docker** – for containerization

---
## Setup and Execution

Follow these steps to start each component of the Anora Streaming Service:

### 1. Start Kafka and Zookeeper

Launch Kafka and Zookeeper for asynchronous messaging between microservices:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties ```

### 2. Start Redis

Run Redis for caching user preferences and other data:

```bash
redis-server

### 3. Run Microservices

Start each Spring Boot microservice in the following order to meet dependency requirements:

## User Service (Authentication & Profiles)

```bash
cd user-service
mvn spring-boot:run ```

## Subscription Service (Payments via Stripe)

```bash
cd subscription-service
mvn spring-boot:run

## Media Service (Video Streaming & Metadata)

```bash
cd media-service
mvn spring-boot:run

## Profile Service (User Personalization & Stats)

```bash
cd profile-service
mvn spring-boot:run

### 4. Expose Stripe Webhook with Ngrok

Expose the Subscription Service’s webhook endpoint (POST /webhook) to Stripe:

```bash
ngrok http 8082

### 5. Run Front-end Server 

```bash
cd frontend
npm install
npm start




Configuration Notes

- ** Verify application.properties or application.yml files in each service for correct database and broker configuration.

- ** Ensure MongoDB and PostgreSQL are running before starting any service.

- ** For production or easier local setup, consider using Docker. Check for a docker-compose.yml file if provided.


Troubleshooting

- ** Port Conflicts: Ensure the required ports (e.g., 8080, 8081, 8082) are not in use.

- ** Database Errors: Check if MongoDB and PostgreSQL are accessible and configured correctly.

- ** Kafka Issues: Make sure both Zookeeper and Kafka are running and accessible.

- ** Ngrok Issues: Restart Ngrok if the tunnel is unresponsive or changes.


