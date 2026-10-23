package com.mss301.order.service;

import com.mss301.order.dto.CreateOrderRequest;
import com.mss301.order.entity.Order;
import com.mss301.order.entity.OrderStatus;
import com.mss301.order.entity.SagaLog;
import com.mss301.order.entity.SagaStatus;
import com.mss301.order.repository.OrderRepository;
import com.mss301.order.repository.SagaLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SagaLogRepository sagaLogRepository;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(orderRepository, sagaLogRepository);
    }

    @Test
    void createPendingOrderInitializesBothStates() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = service.createPendingOrder(new CreateOrderRequest("C1", "P1", 2, 2000));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.PENDING);
    }

    @Test
    void lifecycleMethodsPersistExpectedSagaStates() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setSagaStatus(SagaStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        service.updateSagaStatus(1L, SagaStatus.EXECUTING);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.EXECUTING);

        service.markFailed(1L, "402 - Số dư không đủ");
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.FAILED);
        assertThat(order.getFailureReason()).startsWith("402");

        service.cancelOrder(1L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.COMPENSATED);
    }

    @Test
    void confirmOrderCompletesSaga() {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.PENDING);
        order.setSagaStatus(SagaStatus.EXECUTING);
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));

        service.confirmOrder(2L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getSagaStatus()).isEqualTo(SagaStatus.COMPLETED);
    }

    @Test
    void logCreatesTimestampedSagaEntry() {
        when(sagaLogRepository.save(any(SagaLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SagaLog log = service.log(3L, "CREATE_ORDER", "SUCCESS", "Order PENDING");

        assertThat(log.getOrderId()).isEqualTo(3L);
        assertThat(log.getTime()).isNotNull();
    }
}
