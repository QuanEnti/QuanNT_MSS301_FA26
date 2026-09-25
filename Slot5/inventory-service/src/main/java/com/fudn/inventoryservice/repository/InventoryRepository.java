package com.fudn.inventoryservice.repository;

import com.fudn.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository layer — Spring Data JPA tự sinh implementation.
 *
 * Method convention naming:
 *   existsBySkuCodeAndQuantityIsGreaterThanEqual
 *   → SELECT COUNT(*) > 0 FROM t_inventory
 *     WHERE sku_code = :skuCode AND quantity >= :quantity
 *
 * Dùng GreaterThanEqual (>=) để:
 *   - quantity = 100, request = 100  → true  (đủ hàng exact)
 *   - quantity = 100, request = 101  → false (vượt tồn kho)
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    boolean existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);
}
