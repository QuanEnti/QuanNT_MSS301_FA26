# Assignment 02 — Watch Management using Saga Pattern

> MSS301 — FA2026 · Sinh viên: **QuanNT**
> Java 21 · Spring Boot 4.1.1 · Spring Cloud 2025.1.3 · SQL Server 2022

Bài làm xây dựng bốn project Spring Boot độc lập, dùng mô hình **Database per
Service** và **Saga Orchestration** để giữ nhất quán dữ liệu Watch–Brand mà
không dùng distributed transaction/2PC.

Đề gốc: [MMS301 Assignment 02](../../MMS301_Assignment%2002_Watch%20Management_Saga%20Pattern.docx).

## 1. Checklist TODO và commit

Mỗi TODO con dưới đây tương ứng đúng một Conventional Commit. Cách chia này
giúp từng commit chỉ có một mục đích, dễ review và dễ revert độc lập.

### Phần 0 — Khởi tạo workspace

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-0.1** | Thêm ignore rules, commit template và biến môi trường mẫu | `.gitignore`, `.gitmessage`, `.env.example` | `chore(slot18): TODO-0.1 add workspace config` |
| **TODO-0.2** | Lưu đề Assignment 02 trong Slot18 | `MMS301_Assignment 02_*.docx` | `docs(slot18): TODO-0.2 add assignment brief` |
| **TODO-0.3** | Tạo Maven reactor quản lý bốn module | `pom.xml` | `build(slot18): TODO-0.3 add reactor build` |
| **TODO-0.4** | Scaffold Account Service | `MSAccount_QuanNT/` | `build(account): TODO-0.4 scaffold service` |
| **TODO-0.5** | Scaffold Brand Service | `MSBrand_QuanNT/` | `build(brand): TODO-0.5 scaffold service` |
| **TODO-0.6** | Scaffold Watch Service | `MSWatch_QuanNT/` | `build(watch): TODO-0.6 scaffold service` |
| **TODO-0.7** | Scaffold API Gateway | `APIGateway_QuanNT/` | `build(gateway): TODO-0.7 scaffold service` |

### Phần 1 — Account Service

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-1.1** | Map entity và repository `StoreAccounts` | `model/`, `repository/` | `feat(account): TODO-1.1 map account model` |
| **TODO-1.2** | Nạp năm tài khoản đúng dữ liệu đề | `config/AccountDataSeeder.java` | `feat(account): TODO-1.2 seed account data` |
| **TODO-1.3** | Kiểm tra login, trạng thái active và phát session token | `dto/`, `service/` | `feat(account): TODO-1.3 add session login` |
| **TODO-1.4** | Expose login/logout/session validation và xử lý lỗi | `controller/`, `exception/` | `feat(account): TODO-1.4 expose auth APIs` |
| **TODO-1.5** | Test login đúng, sai password và account inactive | `AuthServiceTest.java` | `test(account): TODO-1.5 cover authentication` |

### Phần 2 — Brand Service

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-2.1** | Map `WatchBrands` và Brand–Watch reference | `model/`, `repository/` | `feat(brand): TODO-2.1 map brand model` |
| **TODO-2.2** | Nạp năm brand và reference của dữ liệu Watch mẫu | `config/BrandDataSeeder.java` | `feat(brand): TODO-2.2 seed brand data` |
| **TODO-2.3** | Cài đặt query brand và reference idempotent | `dto/`, `service/` | `feat(brand): TODO-2.3 add brand operations` |
| **TODO-2.4** | Expose public Brand API và internal reference API | `controller/`, `exception/` | `feat(brand): TODO-2.4 expose brand APIs` |
| **TODO-2.5** | Test create, gọi lặp và chuyển Brand reference | `BrandServiceTest.java` | `test(brand): TODO-2.5 cover brand references` |

### Phần 3 — Watch catalog và CRUD

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-3.1** | Map `Watches`, `WatchCategories` và repository | `model/`, `repository/` | `feat(watch): TODO-3.1 map watch model` |
| **TODO-3.2** | Nạp năm category và mười watch đúng đề | `config/WatchDataSeeder.java` | `feat(watch): TODO-3.2 seed watch catalog` |
| **TODO-3.3** | Tạo DTO và validation name/price/current date | `dto/`, `validation/` | `feat(watch): TODO-3.3 validate requests` |
| **TODO-3.4** | Cài đặt list mới nhất trước và local CRUD transaction | `service/WatchService.java`, `WatchSnapshot.java` | `feat(watch): TODO-3.4 add catalog operations` |
| **TODO-3.5** | Expose Category API và chuẩn hóa lỗi Watch | `controller/CategoryController.java`, `exception/` | `feat(watch): TODO-3.5 expose catalog APIs` |
| **TODO-3.6** | Test validation, CRUD, JPA schema và dữ liệu seed | `src/test/` | `test(watch): TODO-3.6 cover watch catalog` |

### Phần 4 — Saga Orchestration

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-4.1** | Map sáu Saga state, operation và step log | `saga/`, Saga repository/DTO/service | `feat(saga): TODO-4.1 track saga state` |
| **TODO-4.2** | Tạo REST client đồng bộ Brand reference | `client/BrandClient.java` | `feat(saga): TODO-4.2 add brand client` |
| **TODO-4.3** | Orchestrate create/update/delete và compensation retry | `WatchSagaOrchestrator.java`, controller | `feat(saga): TODO-4.3 orchestrate mutations` |
| **TODO-4.4** | Test happy path và các luồng compensation | `WatchSagaOrchestratorTest.java` | `test(saga): TODO-4.4 cover compensation` |

### Phần 5 — API Gateway

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-5.1** | Route Account, Brand, Watch, Category và Saga | `routes/GatewayRoutes.java` | `feat(gateway): TODO-5.1 route services` |
| **TODO-5.2** | Validate Bearer session, chặn non-Admin và chống giả header | `client/`, `filter/` | `feat(gateway): TODO-5.2 enforce admin access` |
| **TODO-5.3** | Test 401, 403 và trusted Admin headers | `AdminAuthorizationFilterTest.java` | `test(gateway): TODO-5.3 cover authorization` |

### Phần 6 — Đóng gói, kiểm thử API và tài liệu

| TODO | Nội dung | File chính | Commit |
|---|---|---|---|
| **TODO-6.1** | Docker hóa SQL Server và bốn application | `docker-compose.yml`, `Dockerfile`, `sqlserver/` | `build(docker): TODO-6.1 containerize system` |
| **TODO-6.2** | Thêm Postman environment, requests và test scripts | `postman/` | `test(postman): TODO-6.2 add API scenarios` |
| **TODO-6.3** | Viết hướng dẫn kiến trúc, chạy, test và Saga | `README.md` | `docs(slot18): TODO-6.3 document assignment` |

Kiểm tra lịch sử:

```bash
git log --oneline --grep="TODO-[0-6]\." -- Slot18
```

## 2. Kiến trúc

```text
Client / Postman
       |
       v
APIGateway_QuanNT :9000
       |
       +-- /api/auth/** ----------------> MSAccount_QuanNT :8081 --> DBAccount
       +-- /api/brands/** --------------> MSBrand_QuanNT   :8082 --> DBBrand
       +-- /api/watches|categories|sagas
                                         MSWatch_QuanNT    :8083 --> DBWatch
                                                |
                                                +-- Brand internal API
```

| Project | Chức năng | Database |
|---|---|---|
| `MSAccount_QuanNT` | `StoreAccounts`, login, token phiên | `DBAccount` |
| `MSBrand_QuanNT` | `WatchBrands`, Brand–Watch reference | `DBBrand` |
| `MSWatch_QuanNT` | `Watches`, `WatchCategories`, Saga state/log | `DBWatch` |
| `APIGateway_QuanNT` | Routing, xác thực token, kiểm tra role Admin | Không có DB |

Các service không tạo foreign key xuyên database. `BrandID` trong `Watches`
là ID logic; Brand Service giữ một reference idempotent để Saga đồng bộ hoặc
hoàn tác khi create/update/delete Watch.

## 3. Luồng xác thực

1. Gọi `POST /api/auth/login` qua Gateway.
2. Account Service kiểm tra username, password và `IsActive`, sau đó trả token.
3. Gửi `Authorization: Bearer <token>` khi create/update/delete Watch.
4. Gateway gọi Account Service để validate token, chỉ cho `Role = 1` đi tiếp.
5. Gateway xóa header `X-Account-*` do client tự gửi và chèn header tin cậy cho
   Watch Service.

GET Watch/Category/Brand không yêu cầu đăng nhập, đúng yêu cầu đề bài.

Tài khoản mẫu:

| Username | Password | Role | Active |
|---|---|---:|---|
| `adminsys` | `@1` | 1 — Administrator | Có |
| `staff02` | `@1` | 2 — Staff | Có |
| `member01` | `@1` | 3 — Member | Có |
| `member02` | `@1` | 3 — Member | Không |
| `staff01` | `@1` | 2 — Staff | Không |

> Password plain text chỉ được giữ để khớp chính xác dữ liệu đề bài. Production
> phải dùng BCrypt/Argon2 và session store bền vững thay vì in-memory token.

## 4. Quy tắc Watch

Create và update đều áp dụng:

- Tất cả field bắt buộc.
- `name` dài hơn 10 ký tự.
- `price >= 100.00`.
- `releaseDate` phải bằng ngày hiện tại, format `yyyy-MM-dd`.
- `stock >= 0`; `categoryId`, `brandId` phải dương.
- Chỉ Administrator được ghi dữ liệu.
- Danh sách sắp xếp `WatchID DESC`, nên item mới nằm đầu danh sách.

Năm category và mười watch trong đề được nạp tự động khi database rỗng. Dữ
liệu seed lịch sử được giữ nguyên; quy tắc giá/ngày chỉ áp dụng cho request tạo
và cập nhật mới.

## 5. Saga Orchestration

Mỗi thao tác ghi chạy thành chuỗi local transaction:

```text
CREATE: create Watch --> register Brand reference --> COMPLETED
UPDATE: update Watch --> transfer Brand reference --> COMPLETED
DELETE: remove Brand reference --> delete Watch --> COMPLETED
```

Nếu một bước lỗi:

```text
FAILED --> COMPENSATING --> chạy ngược các bước đã thành công --> COMPENSATED
```

Compensation là idempotent và retry tối đa 3 lần:

- Create lỗi: unregister Brand, xóa Watch vừa tạo.
- Update lỗi: gỡ Brand mới, khôi phục Brand cũ, restore snapshot Watch.
- Delete lỗi: đăng ký lại Brand reference.

`WatchSagas` lưu 6 trạng thái `PENDING`, `EXECUTING`, `COMPLETED`, `FAILED`,
`COMPENSATING`, `COMPENSATED`; `WatchSagaLogs` lưu từng bước. Orchestrator
không có `@Transactional`, vì mỗi service/database chỉ được commit trong local
transaction riêng.

Mutation thành công trả hai response header:

- `X-Saga-Operation-Id`: ID để tra log.
- `X-Saga-Status`: `COMPLETED`.

## 6. API qua Gateway

Base URL: `http://localhost:9000`

| Method | Endpoint | Quyền | Mô tả |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Đăng nhập |
| GET | `/api/brands` | Public | Danh sách brand |
| GET | `/api/categories` | Public | Danh sách category |
| GET | `/api/watches` | Public | Danh sách watch mới nhất trước |
| GET | `/api/watches/{id}` | Public | Chi tiết watch |
| POST | `/api/watches` | Admin | Tạo watch bằng Saga |
| PUT | `/api/watches/{id}` | Admin | Cập nhật watch bằng Saga |
| DELETE | `/api/watches/{id}` | Admin | Xóa watch bằng Saga |
| GET | `/api/sagas` | Public | Danh sách Saga |
| GET | `/api/sagas/{operationId}` | Public | Trạng thái và step log |

Các endpoint `/api/internal/**` chỉ dùng cho service-to-service và không được
Gateway route ra ngoài.

## 7. Build và test

Yêu cầu:

- JDK 21+
- Maven 3.9+

Tại thư mục `WatchManagement`:

```bash
mvn clean test
```

Kết quả xác nhận gần nhất:

| Module | Test |
|---|---:|
| Account Service | 3 |
| Brand Service | 3 |
| Watch Service + Saga | 9 |
| API Gateway | 3 |
| **Tổng** | **18 pass, 0 fail** |

Watch integration test dùng H2 ở MSSQL compatibility mode, không cần SQL
Server. Test này tạo schema code-first và xác nhận đủ 5 category + 10 watch.

## 8. Chạy toàn bộ bằng Docker

Tạo file môi trường:

```powershell
Copy-Item .env.example .env
```

Build và chạy SQL Server + 4 application:

```bash
docker compose up -d --build
docker compose ps
```

Nếu máy cài executable Compose độc lập:

```bash
docker-compose up -d --build
docker-compose ps
```

SQL Server host port mặc định là `14330`; trong Docker network các service dùng
`sqlserver:1433`. Script `sqlserver/init.sql` tạo ba database, Hibernate tạo
table từ các class `@Entity` khi service khởi động.

Dừng hệ thống:

```bash
docker compose down
```

Thêm `-v` chỉ khi muốn xóa cả volume database và seed lại từ đầu.

## 9. Chạy local bằng IntelliJ

1. Chạy database và init script:

   ```bash
   docker compose up -d sqlserver sqlserver-init
   ```

2. Chạy lần lượt main class:

   - `MsAccountQuanNtApplication`
   - `MsBrandQuanNtApplication`
   - `MsWatchQuanNtApplication`
   - `ApiGatewayQuanNtApplication`

3. Gọi API qua port `9000`, không gọi thẳng service khi kiểm tra phân quyền.

## 10. Postman

Import hai file:

- [`WatchManagement.postman_collection.json`](postman/WatchManagement.postman_collection.json)
- [`WatchManagement.local.postman_environment.json`](postman/WatchManagement.local.postman_environment.json)

Chọn environment **Watch Management - Local**, sau đó chạy Collection Runner
đúng thứ tự. Collection tự:

- tạo biến ngày hiện tại;
- kiểm tra public GET và dữ liệu seed;
- login Admin/Staff và lưu token;
- kiểm tra 401, 403, validation 400;
- create/update/delete thành công;
- xác nhận item mới đứng đầu;
- kiểm tra Saga headers và Saga logs.

Để xem compensation thực tế, dừng `brand-service`, gửi create hợp lệ, sau đó
đọc `GET /api/sagas`: Watch local vừa tạo sẽ bị xóa và Saga kết thúc ở
`COMPENSATED` (request nhận `502 Bad Gateway`).
