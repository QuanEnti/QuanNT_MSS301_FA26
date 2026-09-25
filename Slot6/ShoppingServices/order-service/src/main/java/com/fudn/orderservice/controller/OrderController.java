package com.fudn.orderservice.controller;

import com.fudn.orderservice.dto.OrderRequest;
import com.fudn.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller — nhận POST /api/order và chuyển tiếp đến OrderService.
 * Status 201 Created khi đặt hàng thành công.
 * Status 500 khi hết hàng (RuntimeException chưa handle — TODO: thêm GlobalExceptionHandler).
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.placeOrder(orderRequest);
    }
}
