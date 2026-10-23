package com.mss301.order.controller;

import com.mss301.order.dto.CreateOrderRequest;
import com.mss301.order.entity.Order;
import com.mss301.order.entity.OrderStatus;
import com.mss301.order.entity.SagaLog;
import com.mss301.order.entity.SagaStatus;
import com.mss301.order.repository.OrderRepository;
import com.mss301.order.repository.SagaLogRepository;
import com.mss301.order.saga.OrderSagaOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderSagaOrchestrator orchestrator;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private SagaLogRepository sagaLogRepository;

    private OrderController controller;

    @BeforeEach
    void setUp() {
        controller = new OrderController(orchestrator, orderRepository, sagaLogRepository);
    }

    @Test
    void placeOrderReturnsFinalSagaState() {
        CreateOrderRequest request = new CreateOrderRequest("C1", "P1", 2, 2000);
        Order completed = new Order();
        completed.setId(1L);
        completed.setStatus(OrderStatus.CONFIRMED);
        completed.setSagaStatus(SagaStatus.COMPLETED);
        when(orchestrator.placeOrder(request)).thenReturn(completed);

        Order response = controller.placeOrder(request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.getSagaStatus()).isEqualTo(SagaStatus.COMPLETED);
    }

    @Test
    void findAllAndSagaLogsDelegateToRepositories() {
        Order order = new Order();
        order.setId(2L);
        SagaLog log = new SagaLog(2L, "CREATE_ORDER", "SUCCESS", "Order PENDING");
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(sagaLogRepository.findByOrderIdOrderByIdAsc(2L)).thenReturn(List.of(log));

        assertThat(controller.findAll()).containsExactly(order);
        assertThat(controller.sagaLogs(2L)).containsExactly(log);
    }
}
