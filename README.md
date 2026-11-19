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

## Backend: build and test

You need Java 25 and Docker Desktop running. The tests start a real PostgreSQL container with Testcontainers, so Docker must be on.

1. Build and test the backend from the `backend` folder.

```bash
cd backend
./mvnw -B verify
```

This one command runs three checks: the tests, the formatting check (Spotless with google-java-format) and the coverage check (JaCoCo, at least 80% of lines and 70% of branches). If one of them fails, the build fails. The coverage report is written to `backend/target/site/jacoco/index.html`.

2. If the formatting check fails, fix the files with the formatter. This command changes files, so look at `git diff` afterwards.

```bash
./mvnw spotless:apply
```

3. Every push to GitHub starts the workflow in `.github/workflows/ci.yml`. It runs the same `./mvnw -B verify` on a clean machine in a job called `backend`. The `main` branch only accepts a commit whose `backend` check is green.
