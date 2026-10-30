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
- [ ] TODO-11: add automated service and gateway tests
- [ ] TODO-12: add API scenarios and submission documentation

## Database setup

Run [`database/setup.sql`](database/setup.sql) in SQL Server Management Studio
before starting either data service. The script creates the database, creates
both tables with the constraints from the exam, and inserts three creators when
the `users` table is empty.

