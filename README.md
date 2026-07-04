# TeamCity CI/CD Spring Boot Fixture

This repository is a multi-service backend fixture for validating a TeamCity CI/CD pipeline on a remote host. It is intentionally broader than a hello-world app: it exercises checkout, JDK/Maven setup, multi-module compilation, validation, persistence, unit tests, Spring Boot packaging, coverage report generation, artifact publication, Docker image build, container orchestration, environment-variable injection, service-to-service HTTP, actuator readiness, and runtime smoke checks.

## Services

| Service | Module | Port | Key endpoints |
| --- | --- | --- | --- |
| CICD Service | `cicd-service` | `8082` | `/api/test`, `/api/build-info`, `/api/deployments`, `/actuator/health/readiness` |
| Gateway Service | `gateway-service` | `9090` | `/api/test`, `/api/pipeline-summary`, `/api/routes`, `/actuator/health/readiness` |

`cicd-service` uses an embedded H2 database so the pipeline validates persistence without requiring external infrastructure. `gateway-service` calls `cicd-service` over HTTP so deployment checks catch broken container networking or runtime configuration.

## Local verification

```bash
mvn clean verify
docker compose up --build -d
./scripts/smoke-test.sh
docker compose down
```

If Docker is not part of the pipeline, TeamCity can still run:

```bash
mvn clean verify
java -jar cicd-service/target/cicd-service-0.1.0-SNAPSHOT.jar
java -jar gateway-service/target/gateway-service-0.1.0-SNAPSHOT.jar
```

## TeamCity build-step checklist

1. Use JDK 21.
2. Run `mvn clean verify` from the repository root.
3. Publish artifacts from:
   - `cicd-service/target/*.jar`
   - `gateway-service/target/*.jar`
4. If deployment is container-based, run `docker compose up --build -d` on the target host.
5. Run `./scripts/smoke-test.sh` as the deployment smoke test.

The smoke test fails fast if either service is unavailable, readiness is not `UP`, persistence is broken, or gateway-to-backend HTTP aggregation fails.
