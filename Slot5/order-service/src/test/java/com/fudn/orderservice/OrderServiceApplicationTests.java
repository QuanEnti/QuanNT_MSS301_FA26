package com.fudn.orderservice;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration Test cho Order Service.
 *
 * Dùng TestContainers để khởi động MySQL thật trong Docker.
 * RestAssured gửi HTTP request thật đến app chạy trên RANDOM_PORT.
 *
 * Bao gồm 8 test cases theo order-service_test.md:
 *   TC1 — Đặt hàng thành công (happy path) → 201
 *   TC2 — Đặt hàng số lượng lớn, giá decimal → 201
 *   TC3 — Thiếu skuCode → 201 (null lưu DB, chưa validate)
 *   TC4 — Sai kiểu quantity (chuỗi) → 400
 *   TC5 — Thiếu Content-Type → 415
 *   TC6 — Body rỗng {} → 201 (null fields, chưa validate)
 *   TC7 — Sai method (GET) → 405
 *   TC8 — Sai path /api/orders → 404
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderServiceApplicationTests {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // ─── TC1: Đặt hàng hợp lệ — Happy Path ───────────────────────────────────
    @Test
    @DisplayName("TC1 - Đặt hàng hợp lệ → 201 Created + 'Order Placed Successfully'")
    void shouldSubmitOrder() {
        String body = """
                {
                    "skuCode": "iphone_15",
                    "price": 1000,
                    "quantity": 1
                }
                """;

        var responseBody = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .extract().body().asString();

        assertThat(responseBody, Matchers.is("Order Placed Successfully"));
    }

    // ─── TC2: Số lượng lớn, giá thập phân ────────────────────────────────────
    @Test
    @DisplayName("TC2 - Đặt hàng số lượng lớn, giá decimal → 201")
    void shouldSubmitOrderWithLargeQuantityAndDecimalPrice() {
        String body = """
                {
                    "skuCode": "pixel_8",
                    "price": 899.99,
                    "quantity": 5
                }
                """;

        var responseBody = given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .extract().body().asString();

        assertThat(responseBody, Matchers.is("Order Placed Successfully"));
    }

    // ─── TC3: Thiếu skuCode → 201 (null lưu DB, chưa validate) ──────────────
    @Test
    @DisplayName("TC3 - Thiếu skuCode → 201 (null in DB, no validation yet)")
    void shouldAcceptOrderWithMissingSkuCode() {
        // GHI NHẬN hành vi hiện tại: chưa có @Valid → skuCode = null lưu DB
        // Cải tiến sau: thêm @NotBlank vào OrderRequest → trả 400
        String body = """
                {
                    "price": 1000,
                    "quantity": 1
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(201);
    }

    // ─── TC4: Sai kiểu quantity (chuỗi "abc") → 400 ──────────────────────────
    @Test
    @DisplayName("TC4 - quantity='abc' (sai kiểu) → 400 Bad Request (Jackson parse error)")
    void shouldReturn400WhenQuantityIsNotInteger() {
        String body = """
                {
                    "skuCode": "galaxy_24",
                    "price": 1000,
                    "quantity": "abc"
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    // ─── TC5: Thiếu Content-Type → 415 ───────────────────────────────────────
    @Test
    @DisplayName("TC5 - Không có Content-Type header → 415 Unsupported Media Type")
    void shouldReturn415WhenContentTypeMissing() {
        String body = """
                {
                    "skuCode": "iphone_15",
                    "price": 1000,
                    "quantity": 1
                }
                """;

        // Gửi body dạng text/plain (không có application/json)
        given()
                .body(body)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(415);
    }

    // ─── TC6: Body rỗng {} → 201 (null fields, chưa validate) ───────────────
    @Test
    @DisplayName("TC6 - Body rỗng {} → 201 (all fields null in DB, no validation yet)")
    void shouldAcceptEmptyBody() {
        // GRIT NHẬN: tương tự TC3, chưa có validation
        given()
                .contentType("application/json")
                .body("{}")
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(201);
    }

    // ─── TC7: Sai method (GET thay vì POST) → 405 ────────────────────────────
    @Test
    @DisplayName("TC7 - GET /api/order → 405 Method Not Allowed")
    void shouldReturn405ForGetMethod() {
        given()
                .when()
                .get("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(405);
    }

    // ─── TC8: Sai path /api/orders → 404 ─────────────────────────────────────
    @Test
    @DisplayName("TC8 - POST /api/orders (wrong path) → 404 Not Found")
    void shouldReturn404ForWrongPath() {
        String body = """
                {
                    "skuCode": "iphone_15",
                    "price": 1000,
                    "quantity": 1
                }
                """;

        given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/orders")   // cố tình sai path (thêm 's')
                .then()
                .log().ifValidationFails()
                .statusCode(404);
    }
}
