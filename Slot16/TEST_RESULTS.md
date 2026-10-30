# Slot 16 Test Results

Verified on 2026-10-30 with Java 21, Spring Boot 4.1.0, and Spring Cloud
2025.1.2.

| Project | Tests | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| QuanNTUserService | 7 | 0 | 0 | 0 |
| QuanNTPostService | 7 | 0 | 0 | 0 |
| QuanNTMediaGateway | 1 | 0 | 0 | 0 |
| QuanNTRoomService | 6 | 0 | 0 | 0 |
| QuanNTReservationService | 7 | 0 | 0 | 0 |
| QuanNTHotelGateway | 1 | 0 | 0 | 0 |
| **Total** | **29** | **0** | **0** | **0** |

The service tests cover successful operations, duplicate/not-found/inactive
users, partial update behavior, date validation, creator post limits, calculated
cost, soft delete/cancel, pagination limits, and DTO validation groups. The
gateway test loads the reactive application context and verifies both route IDs
and the CORS filter.

The Hotel tests additionally cover default room/reservation statuses, room
capacity and availability, calculated nightly charges, embedded RoomDTO data,
maintenance/cancellation soft deletes, mandatory DTO fields, and both gateway
routes. The Hotel setup script was executed successfully against SQL Server:
database `MSS301_2026_PE`, tables `rooms` and `reservations`, and four seed rooms.
