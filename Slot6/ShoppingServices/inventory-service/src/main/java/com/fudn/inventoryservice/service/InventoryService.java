package com.fudn.inventoryservice.service;

import com.fudn.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer — xử lý business logic kiểm tra tồn kho.
 *
 * @Transactional(readOnly = true):
 *   - Tối ưu hiệu suất: JPA không track dirty checking
 *   - Đủ cho method chỉ đọc DB (không write)
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Kiểm tra xem SKU có đủ số lượng trong kho không.
     *
     * @param skuCode  mã SKU của sản phẩm
     * @param quantity số lượng cần kiểm tra
     * @return true nếu tồn kho >= quantity, false nếu không đủ hoặc không tồn tại
     */
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        return inventoryRepository
                .existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
    }
}
