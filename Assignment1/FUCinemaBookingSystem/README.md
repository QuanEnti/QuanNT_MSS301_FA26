# FU Cinema Booking System

Hệ thống đặt vé rạp phim được xây dựng theo kiến trúc microservices, gồm Customer Service, Movie Service, Booking Service và API Gateway. Tất cả request từ client và Postman đi qua API Gateway tại `http://localhost:9000`.

## 1. Yêu cầu môi trường

- JDK 21
- Docker Desktop
- Postman 11 hoặc mới hơn
- Các cổng `1433`, `27017`, `3306`, `8081`, `8082`, `8083` và `9000` đang trống

Mỗi service đã có Maven Wrapper nên không cần cài Maven riêng.

## 2. Thành phần hệ thống

| Thành phần | Cổng | Cơ sở dữ liệu |
|---|---:|---|
| API Gateway | 9000 | Không có |
| Customer Service | 8081 | SQL Server – `cinema_customer` |
| Movie Service | 8082 | MongoDB – `cinema_movie` |
| Booking Service | 8083 | MySQL – `cinema_booking` |

Booking Service gọi Movie Service trực tiếp qua `http://localhost:8082` để kiểm tra suất chiếu và số ghế còn lại.

## 3. Khởi động hệ thống

Mở PowerShell tại thư mục `FUCinemaBookingSystem`.

### Bước 1 – Khởi động cơ sở dữ liệu

```powershell
docker compose up -d
docker compose ps
```

Chờ SQL Server, MongoDB và MySQL khởi động hoàn tất trước khi chạy các service.

### Bước 2 – Build bốn ứng dụng

```powershell
Set-Location customer-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..\movie-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..\booking-service
.\mvnw.cmd clean package -DskipTests
Set-Location ..\api-gateway
.\mvnw.cmd clean package -DskipTests
Set-Location ..
```

### Bước 3 – Chạy các service

Mở bốn cửa sổ PowerShell riêng và chạy theo thứ tự dưới đây.

```powershell
# Cửa sổ 1
Set-Location customer-service
.\mvnw.cmd spring-boot:run
```

```powershell
# Cửa sổ 2
Set-Location movie-service
.\mvnw.cmd spring-boot:run
```

```powershell
# Cửa sổ 3
Set-Location booking-service
.\mvnw.cmd spring-boot:run
```

```powershell
# Cửa sổ 4
Set-Location api-gateway
.\mvnw.cmd spring-boot:run
```

Chỉ chạy Postman sau khi cả bốn ứng dụng đã báo khởi động thành công.

## 4. Tài khoản kiểm thử

| Vai trò | Email | Mật khẩu | Ghi chú |
|---|---|---|---|
| ADMIN | `admin@fucinema.com` | `@@abc123@@` | Tài khoản cấu hình sẵn |
| CUSTOMER hoạt động | `an@gmail.com` | `123456` | Dữ liệu seed, token lưu vào `customerToken` |
| CUSTOMER bị khóa | `chi@gmail.com` | `123456` | Dữ liệu seed, dùng kiểm tra đăng nhập bị từ chối |
| CUSTOMER mới | Email sinh tự động trong Collection | `123456` | Request đăng ký lưu token vào `customer2Token` |

Không cần tạo Customer thủ công. Ngoài các tài khoản seed, Collection sinh email ngẫu nhiên để có thể chạy lại nhiều lần.

## 5. Chạy kiểm thử Postman

Hai file cần import nằm trong thư mục `postman`:

- `FUCinemaBookingSystem.postman_collection.json`
- `FUCinema-Local.postman_environment.json`

Thực hiện theo thứ tự:

1. Import cả hai file vào Postman.
2. Chọn environment `FUCinema-Local`.
3. Đảm bảo biến `gateway` có giá trị `http://localhost:9000`.
4. Chuột phải collection `FUCinemaBookingSystem`, chọn **Run collection**.
5. Giữ nguyên thứ tự folder từ `01-Auth` đến `08-Report` và bấm **Run FUCinemaBookingSystem**.
6. Kết quả đạt yêu cầu khi tất cả test đều **Passed** và có `0 Failed`.

Collection gồm 8 folder, 85 request. Request sau sử dụng token và ID được request trước lưu vào environment, vì vậy không đổi thứ tự chạy.

> Test Movie Service unavailable là tình huống kiểm tra thủ công: dừng Movie Service, gửi request tương ứng và khởi động lại service trước khi tiếp tục Collection Runner.

## 6. Kết quả Collection Runner

Chèn ảnh kết quả Collection Runner có đầy đủ tổng số test **Passed** và `0 Failed` vào phần này khi hoàn thiện báo cáo.

Tên ảnh đề xuất: `postman-collection-runner-all-passed.png`.

## 7. Dừng hệ thống

Dừng bốn ứng dụng bằng `Ctrl+C` trong từng cửa sổ PowerShell, sau đó chạy:

```powershell
docker compose down
```

Không thêm tùy chọn `-v` nếu muốn giữ dữ liệu cơ sở dữ liệu để kiểm tra lại.
