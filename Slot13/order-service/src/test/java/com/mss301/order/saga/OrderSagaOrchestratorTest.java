package com.mss301.order.saga;

import com.mss301.order.client.InventoryClient;
import com.mss301.order.client.PaymentClient;
import com.mss301.order.dto.CreateOrderRequest;
import com.mss301.order.entity.Order;
import com.mss301.order.entity.SagaStatus;
import com.mss301.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSagaOrchestratorTest {

    private static final CreateOrderRequest REQUEST =
            new CreateOrderRequest("C1", "P1", 2, 2000);

    @Mock
    private OrderService orderService;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private PaymentClient paymentClient;

    private OrderSagaOrchestrator orchestrator;
    private Order order;

    @BeforeEach
    void setUp() {
        orchestrator = new OrderSagaOrchestrator(orderService, inventoryClient, paymentClient);
        order = new Order();
        order.setId(1L);
        when(orderService.createPendingOrder(REQUEST)).thenReturn(order);
        when(orderService.findById(1L)).thenReturn(order);
    }

    @Test
    void happyPathRunsAllForwardStepsInOrder() {
        orchestrator.placeOrder(REQUEST);

        InOrder calls = inOrder(orderService, inventoryClient, paymentClient);
        calls.verify(orderService).createPendingOrder(REQUEST);
        calls.verify(inventoryClient).reserve(1L, "P1", 2);
        calls.verify(paymentClient).pay(1L, "C1", 2000);
        calls.verify(orderService).confirmOrder(1L);
        verify(inventoryClient, never()).release(1L);
        verify(paymentClient, never()).refund(1L);
    }

    @Test
    void paymentFailureOnlyCompensatesSuccessfulInventoryStep() {
        doThrow(new RestClientException("payment unavailable"))
                .when(paymentClient).pay(1L, "C1", 2000);

        orchestrator.placeOrder(REQUEST);

        verify(orderService).markFailed(1L, "Service không phản hồi: payment unavailable");
        verify(orderService).updateSagaStatus(1L, SagaStatus.COMPENSATING);
        verify(inventoryClient).release(1L);
        verify(paymentClient, never()).refund(1L);
        verify(orderService).cancelOrder(1L);
    }

    @Test
    void compensationUsesLifoAfterPaymentSucceeded() {
        doThrow(new IllegalStateException("database unavailable"))
                .when(orderService).confirmOrder(1L);

        orchestrator.placeOrder(REQUEST);

        InOrder compensation = inOrder(paymentClient, inventoryClient);
        compensation.verify(paymentClient).refund(1L);
        compensation.verify(inventoryClient).release(1L);
        verify(orderService).cancelOrder(1L);
    }

    @Test
    void failedCompensationRetriesThreeTimesAndDoesNotCancelOrder() {
        doThrow(new RestClientException("payment unavailable"))
                .when(paymentClient).pay(1L, "C1", 2000);
        doThrow(new RestClientException("inventory unavailable"))
                .when(inventoryClient).release(1L);

        Order result = orchestrator.placeOrder(REQUEST);

        assertThat(result).isSameAs(order);
        verify(inventoryClient, times(3)).release(1L);
        verify(orderService, times(3))
                .log(org.mockito.ArgumentMatchers.eq(1L),
                        org.mockito.ArgumentMatchers.eq("COMPENSATION"),
                        org.mockito.ArgumentMatchers.eq("FAILED"),
                        anyString());
        verify(orderService, never()).cancelOrder(1L);
    }
}
