# Maks

Maks is a digital wallet web application. Users can create an account, see their balance and send money to other users. The backend is built with Java and Spring Boot, a Python FastAPI service checks transfers for risk, and the frontend is built with React. Everything runs in Docker and will be deployed to AWS. I am building Maks to learn full-stack development step by step.

## Run the database locally

Maks needs PostgreSQL for development. Docker Compose starts it with the same setup on every machine.

1. Install Docker Desktop and make sure it is running.
2. Copy the example environment file. The values in it are fake and only meant for a local database.

```bash
cp .env.example .env
```

3. Start the database in the background.

```bash
docker compose up -d
```

The first start creates two databases, `maks` and `risk`, each with its own owner (`maks_app` and `risk_app`). Nobody else can connect to them. Check that the container is healthy with `docker compose ps`.

4. Connect to the `maks` database.

```bash
docker compose exec postgres psql -U maks_app -d maks
```

5. To delete all local data and start from scratch, remove the containers together with the data volume. This cannot be undone, and the databases are created again on the next start.

```bash
docker compose down -v
```
