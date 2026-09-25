package com.fudn.product_service.dto;

import java.math.BigDecimal;

/**
 * DTO trả dữ liệu về cho client sau khi tạo / lấy sản phẩm.
 * Java Record — bất biến (immutable), tự sinh constructor, getter, equals, hashCode, toString.
 * Truy cập field: response.id(), response.name()… (không cần tiền tố get)
 */
public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price
) {}
