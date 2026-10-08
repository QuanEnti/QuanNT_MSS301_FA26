# Slot 9 - API Documentation with Swagger

This folder contains the Part 1-4 shopping microservices and the Part 5
Springdoc OpenAPI/Swagger implementation.

## Start infrastructure

```powershell
docker compose up -d
```

The shared compose file starts MongoDB (`27017`), MySQL (`3306`) and Keycloak
(`8181`). MySQL initializes both `order_service` and `inventory_service`.

## Start services

Run each command in a separate terminal:

```powershell
cd inventory-service
.\mvnw.cmd spring-boot:run

cd product-service
.\mvnw.cmd spring-boot:run

cd order-service
.\mvnw.cmd spring-boot:run

cd api-gateway
.\mvnw.cmd spring-boot:run
```

Open the aggregated Swagger UI at <http://localhost:9000/swagger-ui.html>.
The Product, Order and Inventory specifications are available from its
service selector.

## Documentation URLs

| Service | Swagger UI | OpenAPI JSON |
|---|---|---|
| Product | `http://localhost:8080/swagger-ui.html` | `http://localhost:8080/api-docs` |
| Order | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/api-docs` |
| Inventory | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/api-docs` |
| Gateway | `http://localhost:9000/swagger-ui.html` | `http://localhost:9000/v3/api-docs` |

The Gateway Swagger resources are public. Business routes under `/api/**`
remain protected by JWT authentication.
