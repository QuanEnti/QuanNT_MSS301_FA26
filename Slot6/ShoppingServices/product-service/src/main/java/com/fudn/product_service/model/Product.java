package com.fudn.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Domain Model — map sang MongoDB collection "product".
 * Dùng @Document để Spring Data MongoDB nhận ra class này.
 * Lombok tự sinh getter/setter, equals, hashCode, toString, builder.
 */
@Document(value = "product")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /** MongoDB ObjectId — tự sinh nếu không truyền vào */
    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal price;
}
