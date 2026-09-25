package com.fudn.orderservice.dto;

import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu đặt hàng từ client.
 * Java Record — immutable, tự sinh constructor/getter/equals/hashCode.
 */
public record OrderRequest(Long id, String skuCode, BigDecimal price, Integer quantity) {
}
