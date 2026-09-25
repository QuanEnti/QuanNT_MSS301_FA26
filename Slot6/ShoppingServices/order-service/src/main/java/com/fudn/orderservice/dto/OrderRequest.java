package com.fudn.orderservice.dto;

import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu từ client khi đặt hàng (POST /api/order).
 * Java Record — immutable, tự sinh constructor/getter/equals.
 */
public record OrderRequest(
        String skuCode,
        BigDecimal price,
        Integer quantity
) {}
