package com.fudn.orderservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity ánh xạ bảng t_orders trong MySQL.
 * Flyway tạo bảng qua V1__init_order.sql.
 */
@Entity
@Table(name = "t_orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
