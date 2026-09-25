package com.fudn.inventoryservice;

import io.restassured.RestAssured;
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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test cho Inventory Service.
 *
 * Dùng TestContainers để khởi động MySQL thật trong Docker.
 * @ServiceConnection tự inject connection URL vào Spring context.
 * RestAssured gửi HTTP request tới app đang chạy trên RANDOM_PORT.
 *
 * Bao gồm 10 test case theo inventory-service_test.md:
 *   TC1  — Đủ hàng (quantity = tồn kho)
 *   TC2  — Không đủ hàng (quantity > tồn kho)
 *   TC3  — Boundary: quantity == tồn kho (GreaterThanEqual)
 *   TC4  — Boundary: quantity = tồn kho + 1
 *   TC5  — SKU không tồn tại
 *   TC6  — Thiếu param quantity → 400
 *   TC7  — Thiếu param skuCode → 400
 *   TC8  — quantity sai kiểu (chuỗi) → 400
 *   TC9  — quantity âm (edge case, hiện tại trả true)
 *   TC10 — Sai HTTP method (POST) → 405
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class InventoryServiceApplicationTests {

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

    // ─── TC1: Đủ hàng — quantity = tồn kho ────────────────────────────────────
    @Test
    @DisplayName("TC1 - isInStock returns true when quantity = stock (100)")
    void shouldReturnTrueWhenInStock() {
        var result = given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertTrue(result, "iphone_15 quantity=1 should be in stock");
    }

    // ─── TC2: Không đủ hàng ────────────────────────────────────────────────────
    @Test
    @DisplayName("TC2 - isInStock returns false when quantity > stock (1000)")
    void shouldReturnFalseWhenOutOfStock() {
        var result = given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=1000")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertFalse(result, "iphone_15 quantity=1000 should be out of stock");
    }

    // ─── TC3: Boundary — quantity đúng bằng tồn kho (>=) ─────────────────────
    @Test
    @DisplayName("TC3 - Boundary: quantity == stock (100) returns true (GreaterThanEqual)")
    void shouldReturnTrueAtExactStockBoundary() {
        var result = given()
                .when()
                .get("/api/inventory?skuCode=pixel_8&quantity=100")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertTrue(result,
                "pixel_8 quantity=100 must return true — GreaterThanEqual (>=) check");
    }

    // ─── TC4: Boundary — quantity vượt tồn kho đúng 1 ────────────────────────
    @Test
    @DisplayName("TC4 - Boundary: quantity = stock + 1 (101) returns false")
    void shouldReturnFalseWhenOneAboveStock() {
        var result = given()
                .when()
                .get("/api/inventory?skuCode=pixel_8&quantity=101")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertFalse(result, "pixel_8 quantity=101 should be out of stock");
    }

    // ─── TC5: SKU không tồn tại → false, không phải lỗi ──────────────────────
    @Test
    @DisplayName("TC5 - Non-existent SKU returns false (not 404/500)")
    void shouldReturnFalseForNonExistentSku() {
        var result = given()
                .when()
                .get("/api/inventory?skuCode=not_exist_sku&quantity=1")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertFalse(result, "Unknown SKU should return false, not throw exception");
    }

    // ─── TC6: Thiếu param quantity → 400 ──────────────────────────────────────
    @Test
    @DisplayName("TC6 - Missing required param 'quantity' returns 400")
    void shouldReturn400WhenQuantityMissing() {
        given()
                .when()
                .get("/api/inventory?skuCode=iphone_15")
                .then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    // ─── TC7: Thiếu param skuCode → 400 ───────────────────────────────────────
    @Test
    @DisplayName("TC7 - Missing required param 'skuCode' returns 400")
    void shouldReturn400WhenSkuCodeMissing() {
        given()
                .when()
                .get("/api/inventory?quantity=10")
                .then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    // ─── TC8: quantity sai kiểu (chuỗi) → 400 ────────────────────────────────
    @Test
    @DisplayName("TC8 - Invalid quantity type (string 'abc') returns 400")
    void shouldReturn400WhenQuantityIsNotInteger() {
        given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=abc")
                .then()
                .log().ifValidationFails()
                .statusCode(400);
    }

    // ─── TC9: quantity âm — edge case (hiện tại trả true) ────────────────────
    @Test
    @DisplayName("TC9 - Negative quantity edge case (currently returns true, no validation)")
    void negativeQuantityCurrentlyReturnsTrue() {
        // EDGE CASE: chưa có validate quantity >= 0
        // quantity = -5 thỏa mãn GreaterThanEqual(-5) vì 100 >= -5
        // → Đây là điểm cải tiến: nên thêm @Min(1) hoặc check trong service
        var result = given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=-5")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().as(Boolean.class);

        assertTrue(result, "TC9 documents current behavior: negative quantity returns true (no validation yet)");
    }

    // ─── TC10: Sai HTTP method → 405 ─────────────────────────────────────────
    @Test
    @DisplayName("TC10 - POST instead of GET returns 405 Method Not Allowed")
    void shouldReturn405ForPostMethod() {
        given()
                .when()
                .post("/api/inventory?skuCode=iphone_15&quantity=1")
                .then()
                .log().ifValidationFails()
                .statusCode(405);
    }

    // ─── Extra: Kiểm tra tất cả 4 SKU mẫu trong V2 đều tồn tại ───────────────
    @Test
    @DisplayName("Extra - All 4 seed SKUs from V2 migration are in stock with quantity=100")
    void allSeedSkusShouldBeInStock() {
        String[] skus = {"iphone_15", "pixel_8", "galaxy_24", "oneplus_12"};

        for (String sku : skus) {
            var result = given()
                    .when()
                    .get("/api/inventory?skuCode=" + sku + "&quantity=100")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract().as(Boolean.class);

            assertTrue(result, "SKU " + sku + " with quantity=100 should be in stock");
        }
    }

    // ─── Extra: Các SKU khác nhau đều trả false khi quantity quá lớn ─────────
    @Test
    @DisplayName("Extra - All seed SKUs return false when quantity > 100")
    void allSeedSkusShouldBeOutOfStockForLargeQuantity() {
        String[] skus = {"iphone_15", "pixel_8", "galaxy_24", "oneplus_12"};

        for (String sku : skus) {
            var result = given()
                    .when()
                    .get("/api/inventory?skuCode=" + sku + "&quantity=101")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .extract().as(Boolean.class);

            assertFalse(result, "SKU " + sku + " with quantity=101 should be out of stock");
        }
    }
}
