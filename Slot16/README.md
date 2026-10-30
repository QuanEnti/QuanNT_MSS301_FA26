# Slot 16 - MSS301 Practical Exam Sample

This folder contains both practical exam implementations.

Sample Media exam:

- `QuanNTUserService` - user management on port `8081`
- `QuanNTPostService` - sponsored post management on port `8082`
- `QuanNTMediaGateway` - API gateway on port `8080`

Hotel exam:

- `QuanNTRoomService` - room management on port `8081`
- `QuanNTReservationService` - reservation management on port `8082`
- `QuanNTHotelGateway` - API gateway on port `8080`

All services target JDK 21. The Media services use `MSS301_2026_PE_MEDIA`; the
Hotel services use `MSS301_2026_PE`. Hibernate schema generation is disabled
with `spring.jpa.hibernate.ddl-auto=none` in every data service.

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
- [x] TODO-13: add the Hotel SQL Server database schema and seed data
- [x] TODO-14: bootstrap RoomService and map the room entity
- [x] TODO-15: add room DTOs and repository
- [x] TODO-16: implement room business operations
- [x] TODO-17: expose and configure the RoomService API
- [x] TODO-18: bootstrap ReservationService and map the reservation entity
- [x] TODO-19: add reservation DTOs and repository
- [x] TODO-20: connect ReservationService to RoomService with OpenFeign
- [x] TODO-21: implement reservation business operations
- [x] TODO-22: expose and configure the ReservationService API
- [x] TODO-23: configure HotelGateway routes, CORS, Security, and Swagger
- [x] TODO-24: add automated Hotel service and gateway tests
- [x] TODO-25: add Hotel API scenarios and documentation

## Database setup

Run the script belonging to the selected exam before starting its services:

- [`database/setup.sql`](database/setup.sql) creates the Media database, tables,
  constraints, and creator seed data.
- [`hotel-database/setup.sql`](hotel-database/setup.sql) creates the Hotel
  database, `rooms`/`reservations` tables, constraints, foreign key, and room
  seed data.

## Run locally

Requirements: JDK 21, Maven 3.9+, and SQL Server 2016+ listening on port 1433.
The exam credentials in both data services are `sa` / `sa`.

Run one exam set at a time because both sets use ports 8080-8082. Start each
application from its own terminal. For the Hotel exam:

```powershell
cd QuanNTRoomService
mvn spring-boot:run

cd QuanNTReservationService
mvn spring-boot:run

cd QuanNTHotelGateway
mvn spring-boot:run
```

Useful URLs after all three applications are running:

| URL | Purpose |
| --- | --- |
| `http://localhost:8080/api/rooms` | Room API through the gateway |
| `http://localhost:8080/api/reservations` | Reservation API through the gateway |
| `http://localhost:8080/swagger-ui.html` | Aggregated Swagger UI |
| `http://localhost:8081/swagger-ui/index.html` | RoomService Swagger UI |
| `http://localhost:8082/swagger-ui/index.html` | ReservationService Swagger UI |

The business status mapping is `1` = success, `2` = validation, `3` = duplicate
or room number, `4` = not found, `5` = unavailable dependency, and `0` =
server error.

## Verification

Run the test suite in every project:

```powershell
mvn -f QuanNTUserService/pom.xml test
mvn -f QuanNTPostService/pom.xml test
mvn -f QuanNTMediaGateway/pom.xml test
mvn -f QuanNTRoomService/pom.xml test
mvn -f QuanNTReservationService/pom.xml test
mvn -f QuanNTHotelGateway/pom.xml test
```

See [`TEST_RESULTS.md`](TEST_RESULTS.md) for the verified result and
[`POSTMAN_RUN.md`](POSTMAN_RUN.md) and
[`HOTEL_POSTMAN_RUN.md`](HOTEL_POSTMAN_RUN.md) for the end-to-end collections.

## Submission checklist

1. Confirm the selected database script creates its database and both tables on
   a clean SQL Server instance.
2. Start all three applications and run the Postman collection through port
   `8080`.
3. Run `mvn clean` inside each project so no `target/` directory is submitted.
4. Keep the three project names and packages unchanged.
5. Zip the three projects and database script belonging to the selected exam;
   name the ZIP with the StudentID prefix.

