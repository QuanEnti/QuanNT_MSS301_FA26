package com.fudn.orderservice.service;

import com.fudn.orderservice.client.InventoryClient;
import com.fudn.orderservice.dto.OrderRequest;
import com.fudn.orderservice.model.Order;
import com.fudn.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer — business logic đặt hàng.
 *
 * Flow placeOrder():
 *   1. Gọi InventoryClient.isInStock() qua OpenFeign (HTTP tới Inventory Service)
 *   2. Nếu true  → tạo Order + save → return "Order Placed Successfully"
 *   3. Nếu false → throw RuntimeException (không lưu DB nhờ @Transactional rollback)
 *
 * @Transactional: nếu xảy ra RuntimeException trong khi save → rollback toàn bộ.
 * Đảm bảo không bao giờ có đơn hàng bị lưu nửa vời.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;  // OpenFeign proxy — Spring inject tự động

    /**
     * Đặt hàng: kiểm tra tồn kho → lưu đơn nếu đủ hàng.
     *
     * @param orderRequest thông tin đơn hàng từ client
     * @return thông báo thành công
     * @throws RuntimeException nếu hết hàng
     */
    public String placeOrder(OrderRequest orderRequest) {
        // Bước 1: Gọi sang Inventory Service qua OpenFeign
        boolean inStock = inventoryClient.isInStock(
                orderRequest.skuCode(),
                orderRequest.quantity()
        );

        if (inStock) {
            // Bước 2: Đủ hàng → tạo và lưu đơn hàng
            Order order = mapToOrder(orderRequest);
            orderRepository.save(order);
            return "Order Placed Successfully";
        } else {
            // Bước 3: Hết hàng → throw exception, @Transactional rollback
            throw new RuntimeException(
                    "Product with Skucode " + orderRequest.skuCode() + " is not in stock"
            );
        }
    }

    private static Order mapToOrder(OrderRequest req) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setSkuCode(req.skuCode());
        order.setPrice(req.price());
        order.setQuantity(req.quantity());
        return order;
    }
}
