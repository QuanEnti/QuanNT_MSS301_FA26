# Hotel Postman Run Guide

Import [`postman/Slot16_PE_Hotel.postman_collection.json`](postman/Slot16_PE_Hotel.postman_collection.json)
into Postman.

1. Execute `hotel-database/setup.sql` in SQL Server Management Studio.
2. Start `QuanNTRoomService` on 8081, `QuanNTReservationService` on 8082,
   then `QuanNTHotelGateway` on 8080.
3. Keep `baseUrl` as `http://localhost:8080` and run the collection in order.

The runner creates a unique room, verifies duplicate and partial-update rules,
creates a reservation, verifies the embedded room and calculated amount, then
cancels the reservation and moves the test room to maintenance.
