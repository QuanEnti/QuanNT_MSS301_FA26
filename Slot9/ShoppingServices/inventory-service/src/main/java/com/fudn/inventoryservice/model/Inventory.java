package com.fudn.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng t_inventory trong MySQL.
 * Flyway tạo bảng này qua V1__init.sql.
 *
 * Dùng @Getter/@Setter thay @Data để tránh circular reference
 * khi log/equals với entity có quan hệ phức tạp (best practice JPA).
 */
@Entity
@Table(name = "t_inventory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skuCode;

    private Integer quantity;
}
