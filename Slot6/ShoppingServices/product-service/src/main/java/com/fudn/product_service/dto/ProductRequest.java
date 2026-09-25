package com.fudn.product_service.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu từ client khi tạo sản phẩm (POST /api/products).
 * Dùng Lombok @Builder để test có thể dùng: ProductRequest.builder()...build().
 *
 * Tách biệt DTO khỏi Domain Model giúp:
 *  - Không lộ thông tin nội bộ (vd: @Id)
 *  - Linh hoạt validate input riêng biệt
 *  - Dễ thay đổi API mà không ảnh hưởng DB schema
 */
@Builder
public record ProductRequest(
        String name,
        String description,
        BigDecimal price
) {}
