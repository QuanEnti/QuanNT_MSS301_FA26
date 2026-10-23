package com.mss301.order.saga;

import com.mss301.order.client.InventoryClient;
import com.mss301.order.client.PaymentClient;
import com.mss301.order.dto.CreateOrderRequest;
import com.mss301.order.entity.Order;
import com.mss301.order.entity.SagaStatus;
import com.mss301.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSagaOrchestrator {

    private final OrderService orderService;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    private final RetryTemplate compensationRetry = new RetryTemplate(
            RetryPolicy.builder()
                    .includes(RestClientException.class)
                    .maxRetries(2)
                    .delay(Duration.ofSeconds(1))
                    .build()
    );

    @Value("${saga.demo.payment-delay-ms:0}")
    private long paymentDelayMs;

    public Order placeOrder(CreateOrderRequest request) {
        validate(request);

        Order order = orderService.createPendingOrder(request);
        Long orderId = order.getId();
        orderService.log(orderId, "CREATE_ORDER", "SUCCESS", "Order PENDING");
        orderService.updateSagaStatus(orderId, SagaStatus.EXECUTING);

        Deque<Runnable> compensations = new ArrayDeque<>();

        try {
            inventoryClient.reserve(orderId, request.productId(), request.quantity());
            orderService.log(orderId, "RESERVE_INVENTORY", "SUCCESS",
                    "Đã giữ " + request.quantity() + " " + request.productId());
            compensations.push(() -> inventoryClient.release(orderId));

            delayBeforePayment(orderId);

            paymentClient.pay(orderId, request.customerId(), request.amount());
            orderService.log(orderId, "PROCESS_PAYMENT", "SUCCESS",
                    "Đã thu " + request.amount());
            compensations.push(() -> paymentClient.refund(orderId));

            orderService.confirmOrder(orderId);
            orderService.log(orderId, "CONFIRM_ORDER", "SUCCESS", "Saga COMPLETED");
        } catch (RuntimeException exception) {
            String reason = extractReason(exception);
            log.warn("Saga order={} thất bại: {}", orderId, reason);
            orderService.log(orderId, "SAGA", "FAILED", reason);
            orderService.markFailed(orderId, reason);
            compensate(orderId, compensations);
        }

        return orderService.findById(orderId);
    }

    private void compensate(Long orderId, Deque<Runnable> compensations) {
        orderService.updateSagaStatus(orderId, SagaStatus.COMPENSATING);
        boolean allSucceeded = true;

        while (!compensations.isEmpty()) {
            allSucceeded &= runWithRetry(orderId, compensations.pop());
        }

        if (!allSucceeded) {
            log.error("Compensation cho order={} chưa xong, cần xử lý thủ công", orderId);
            return;
        }

        orderService.cancelOrder(orderId);
        orderService.log(orderId, "CANCEL_ORDER", "SUCCESS", "Saga COMPENSATED");
    }

    private boolean runWithRetry(Long orderId, Runnable action) {
        AtomicInteger attempt = new AtomicInteger();
        try {
            compensationRetry.execute(() -> {
                int currentAttempt = attempt.incrementAndGet();
                try {
                    action.run();
                } catch (RestClientException exception) {
                    orderService.log(orderId, "COMPENSATION", "FAILED",
                            "Lần thử " + currentAttempt + ": " + exception.getMessage());
                    throw exception;
                }
                orderService.log(orderId, "COMPENSATION", "SUCCESS",
                        "Lần thử " + currentAttempt);
                return null;
            });
            return true;
        } catch (RetryException exception) {
            return false;
        }
    }

    private String extractReason(RuntimeException exception) {
        if (exception instanceof RestClientResponseException responseException) {
            try {
                Map<?, ?> response = responseException.getResponseBodyAs(Map.class);
                if (response != null && response.get("message") != null) {
                    return responseException.getStatusCode().value() + " - " + response.get("message");
                }
            } catch (RuntimeException ignored) {
                // Fallback below still preserves the HTTP status.
            }
            return responseException.getStatusCode().toString();
        }
        if (exception instanceof RestClientException) {
            return "Service không phản hồi: " + exception.getMessage();
        }
        return "Lỗi nội bộ: " + exception.getMessage();
    }

    private void validate(CreateOrderRequest request) {
        if (request.customerId() == null || request.customerId().isBlank()
                || request.productId() == null || request.productId().isBlank()
                || request.quantity() <= 0 || request.amount() <= 0) {
            throw new IllegalArgumentException(
                    "customerId, productId, quantity dương và amount dương là bắt buộc"
            );
        }
    }

    private void delayBeforePayment(Long orderId) {
        if (paymentDelayMs <= 0) {
            return;
        }
        log.warn("[DEMO] Chờ {} ms trước khi thanh toán order={}", paymentDelayMs, orderId);
        try {
            Thread.sleep(paymentDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Luồng Saga bị gián đoạn", exception);
        }
    }
}
