package com.mss301.order.service;

import com.mss301.order.dto.CreateOrderRequest;
import com.mss301.order.entity.Order;
import com.mss301.order.entity.OrderStatus;
import com.mss301.order.entity.SagaLog;
import com.mss301.order.entity.SagaStatus;
import com.mss301.order.repository.OrderRepository;
import com.mss301.order.repository.SagaLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SagaLogRepository sagaLogRepository;

    @Transactional
    public Order createPendingOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setAmount(request.amount());
        order.setStatus(OrderStatus.PENDING);
        order.setSagaStatus(SagaStatus.PENDING);
        return orderRepository.save(order);
    }

    @Transactional
    public void updateSagaStatus(Long orderId, SagaStatus sagaStatus) {
        Order order = requiredOrder(orderId);
        order.setSagaStatus(sagaStatus);
    }

    @Transactional
    public void markFailed(Long orderId, String reason) {
        Order order = requiredOrder(orderId);
        order.setSagaStatus(SagaStatus.FAILED);
        order.setFailureReason(reason);
    }

    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = requiredOrder(orderId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setSagaStatus(SagaStatus.COMPLETED);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = requiredOrder(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        order.setSagaStatus(SagaStatus.COMPENSATED);
    }

    @Transactional
    public SagaLog log(Long orderId, String step, String result, String message) {
        return sagaLogRepository.save(new SagaLog(orderId, step, result, message));
    }

    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return requiredOrder(orderId);
    }

    private Order requiredOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy order " + orderId));
    }
}
