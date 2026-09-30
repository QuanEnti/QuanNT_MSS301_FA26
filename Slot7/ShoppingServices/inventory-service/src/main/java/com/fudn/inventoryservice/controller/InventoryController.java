package com.fudn.inventoryservice.controller;

import com.fudn.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller — Inventory Service.
 *
 * Endpoint: GET /api/inventory?skuCode=xxx&quantity=yyy
 * Response: boolean (true = in stock, false = out of stock)
 * Status  : 200 OK
 *
 * Spring tự trả 400 Bad Request nếu thiếu @RequestParam bắt buộc.
 * Spring tự trả 405 Method Not Allowed nếu dùng method sai (POST...).
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Kiểm tra tồn kho theo skuCode và quantity.
     *
     * @param skuCode  mã sản phẩm (required)
     * @param quantity số lượng cần đặt (required, phải là Integer)
     * @return true nếu đủ hàng, false nếu không đủ
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(
            @RequestParam String skuCode,
            @RequestParam Integer quantity
    ) {
        return inventoryService.isInStock(skuCode, quantity);
    }
}
