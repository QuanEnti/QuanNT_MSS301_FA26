package com.fudn.orderservice;

import com.fudn.orderservice.stub.InventoryStubs;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration Test — Order Service với WireMock stub cho Inventory Service.
 *
 * Flow:
 *   1. TestContainers khởi động MySQL thật
 *   2. @AutoConfigureWireMock(port=0) khởi động WireMock ở port ngẫu nhiên
 *   3. application.properties (test) trỏ inventory.url → WireMock port
 *   4. Feign client tự động gọi WireMock thay vì Inventory Service thật
 *   5. RestAssured gọi HTTP tới Order Service → Order Service gọi WireMock → verify kết quả
 *
 * Test cases:
 *   TC1 — Đủ hàng → 201 Created (stub trả true)
 *   TC2 — Hết hàng → 500 RuntimeException (stub trả false)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)   // WireMock random port → wiremock.server.port
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

    // ─── TC1: Đặt hàng thành công — Inventory trả true ───────────────────────
    @Test
    @DisplayName("TC1 - shouldSubmitOrder: inventory in stock → 201 Created")
    void shouldSubmitOrder() {
        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 1
                }
                """;

        // Đăng ký stub: khi Feign gọi /api/inventory?skuCode=iphone_15&quantity=1 → trả true
        InventoryStubs.stubInventoryCall("iphone_15", 1);

        var responseBody = given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().asString();

        assertThat(responseBody, Matchers.is("Order Placed Successfully"));
    }

    // ─── TC2: Hết hàng — Inventory trả false → 500 ───────────────────────────
    @Test
    @DisplayName("TC2 - shouldRejectOrderWhenOutOfStock: inventory out of stock → 500")
    void shouldRejectOrderWhenOutOfStock() {
        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 101
                }
                """;

        // Stub: inventory báo hết hàng (false)
        InventoryStubs.stubInventoryOutOfStock("iphone_15", 101);

        given()
                .contentType("application/json")
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .log().ifValidationFails()
                .statusCode(500); // RuntimeException chưa được handle → 500
    }
}
