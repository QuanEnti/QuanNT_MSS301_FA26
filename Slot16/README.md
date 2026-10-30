# Slot 16 - MSS301 Practical Exam Sample

This folder contains the three applications required by the sample exam:

- `QuanNTUserService` - user management on port `8081`
- `QuanNTPostService` - sponsored post management on port `8082`
- `QuanNTMediaGateway` - API gateway on port `8080`

All services target JDK 21. The two data services share the SQL Server database
`MSS301_2026_PE_MEDIA`; Hibernate schema generation is disabled with
`spring.jpa.hibernate.ddl-auto=none`.

## Implementation TODOs

- [x] TODO-0: add the SQL Server database schema and seed data
- [x] TODO-1: bootstrap UserService and map the user entity
- [x] TODO-2: add user DTOs and repository
- [x] TODO-3: implement user business operations
- [x] TODO-4: expose and configure the UserService API
- [x] TODO-5: bootstrap PostService and map the post entity
- [x] TODO-6: add post DTOs and repository
- [x] TODO-7: connect PostService to UserService with OpenFeign
- [x] TODO-8: implement post business operations
- [x] TODO-9: expose and configure the PostService API
- [x] TODO-10: configure MediaGateway routes, CORS, and Swagger
- [x] TODO-11: add automated service and gateway tests
- [x] TODO-12: add API scenarios and submission documentation

## Database setup

Run [`database/setup.sql`](database/setup.sql) in SQL Server Management Studio
before starting either data service. The script creates the database, creates
both tables with the constraints from the exam, and inserts three creators when
the `users` table is empty.

## Run locally

Requirements: JDK 21, Maven 3.9+, and SQL Server 2016+ listening on port 1433.
The exam credentials in both data services are `sa` / `sa`.

Run the applications in this order, each from its own terminal:

```powershell
cd QuanNTUserService
mvn spring-boot:run

cd QuanNTPostService
mvn spring-boot:run

cd QuanNTMediaGateway
mvn spring-boot:run
```

Useful URLs after all three applications are running:

| URL | Purpose |
| --- | --- |
| `http://localhost:8080/api/users` | User API through the gateway |
| `http://localhost:8080/api/posts` | Post API through the gateway |
| `http://localhost:8080/swagger-ui.html` | Aggregated Swagger UI |
| `http://localhost:8081/swagger-ui/index.html` | UserService Swagger UI |
| `http://localhost:8082/swagger-ui/index.html` | PostService Swagger UI |

The business status mapping is `1` = success, `2` = validation, `3` = duplicate
username, `4` = not found, `5` = user not active, and `0` = server error.

## Verification

Run the test suite in every project:

```powershell
mvn -f QuanNTUserService/pom.xml test
mvn -f QuanNTPostService/pom.xml test
mvn -f QuanNTMediaGateway/pom.xml test
```

See [`TEST_RESULTS.md`](TEST_RESULTS.md) for the verified result and
[`POSTMAN_RUN.md`](POSTMAN_RUN.md) for the end-to-end API collection.

## Submission checklist

1. Confirm `database/setup.sql` creates the database and both tables on a clean
   SQL Server instance.
2. Start all three applications and run the Postman collection through port
   `8080`.
3. Run `mvn clean` inside each project so no `target/` directory is submitted.
4. Keep the three project names and packages unchanged.
5. Zip the folder containing `QuanNTUserService`, `QuanNTPostService`,
   `QuanNTMediaGateway`, and `database`; name the ZIP with the StudentID prefix.

