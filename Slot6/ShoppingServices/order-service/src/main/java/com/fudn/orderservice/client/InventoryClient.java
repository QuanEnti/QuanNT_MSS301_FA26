package com.fudn.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * OpenFeign declarative HTTP client giao tiếp với Inventory Service.
 *
 * Spring tự sinh implementation (dynamic proxy) khi khởi động.
 *
 * @FeignClient attributes:
 *   value = "inventory"           — tên logic service (dùng khi có Eureka ở Part 3)
 *   url = "${inventory.url}"      — URL cứng, externalized ra application.properties
 *                                    Dev: http://localhost:8082
 *                                    Test: http://localhost:${wiremock.server.port}
 *
 * Khi có Eureka (Part 3): xóa attribute url, Feign tự resolve qua service discovery.
 */
@FeignClient(value = "inventory", url = "${inventory.url}")
public interface InventoryClient {

    /**
     * Gọi GET /api/inventory?skuCode=xxx&quantity=yyy tới Inventory Service.
     *
     * @param skuCode  mã sản phẩm
     * @param quantity số lượng cần kiểm tra
     * @return true nếu tồn kho >= quantity, false nếu không đủ
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);
}
