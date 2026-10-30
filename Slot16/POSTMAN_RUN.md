# Postman Run Guide

Import [`postman/Slot16_PE_Sample.postman_collection.json`](postman/Slot16_PE_Sample.postman_collection.json)
into Postman.

Before running the collection:

1. Execute `database/setup.sql` in SQL Server Management Studio.
2. Start UserService on 8081, PostService on 8082, then MediaGateway on 8080.
3. Leave the collection variable `baseUrl` as `http://localhost:8080`.
4. Use Collection Runner and keep the request order unchanged.

The runner generates a unique username, carries the created `userId` and
`postId` between requests, verifies CRUD/filter/validation responses, cancels
the post, deactivates the user, and finally verifies that the inactive creator
cannot receive a new campaign.

Expected result: every script in the collection passes. The final two requests
intentionally change the generated user to `DEACTIVATED`; the three seed users
are not modified.
