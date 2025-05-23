## Setup and Execution

Follow these steps to start each component of the Anora Streaming Service:

### 1. Start Kafka and Zookeeper

Launch Kafka and Zookeeper for asynchronous messaging between microservices:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

### 2. Start Redis

Run Redis for caching user preferences and other data:

```bash
redis-server
```

### 3. Run Microservices

Start each Spring Boot microservice in the following order to meet dependency requirements:

#### User Service (Authentication & Profiles)

```bash
cd user-service
mvn spring-boot:run
```

#### Subscription Service (Payments via Stripe)

```bash
cd subscription-service
mvn spring-boot:run
```

#### Media Service (Video Streaming & Metadata)

```bash
cd media-service
mvn spring-boot:run
```

#### Profile Service (User Personalization & Stats)

```bash
cd profile-service
mvn spring-boot:run
```

### 4. Expose Stripe Webhook with Ngrok

Expose the Subscription Service’s webhook endpoint (POST /webhook) to Stripe:

```bash
ngrok http 8082
```

### 5. Run Front-end Server 

```bash
cd frontend
npm install
npm start
```

## Configuration Notes

- **Verify** `application.properties` or `application.yml` files in each service for correct database and broker configuration.
- **Ensure** MongoDB and PostgreSQL are running before starting any service.
- **For production** or easier local setup, consider using Docker. Check for a `docker-compose.yml` file if provided.

## Troubleshooting

- **Port Conflicts**: Ensure the required ports (e.g., 8080, 8081, 8082) are not in use.
- **Database Errors**: Check if MongoDB and PostgreSQL are accessible and configured correctly.
- **Kafka Issues**: Make sure both Zookeeper and Kafka are running and accessible.
- **Ngrok Issues**: Restart Ngrok if the tunnel is unresponsive or changes.
