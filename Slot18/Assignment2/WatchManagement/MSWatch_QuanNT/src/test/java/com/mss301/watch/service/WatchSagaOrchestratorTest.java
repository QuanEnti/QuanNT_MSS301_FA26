package com.mss301.watch.service;

import com.mss301.watch.client.BrandClient;
import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.exception.ApiException;
import com.mss301.watch.saga.SagaOperationType;
import com.mss301.watch.saga.SagaStatus;
import com.mss301.watch.saga.WatchSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchSagaOrchestratorTest {

    @Mock
    private WatchService watchService;

    @Mock
    private BrandClient brandClient;

    @Mock
    private SagaService sagaService;

    @InjectMocks
    private WatchSagaOrchestrator orchestrator;

    @Test
    void createCompletesLocalAndRemoteStepsInOrder() {
        WatchRequest request = request(2);
        WatchResponse created = response(11, 2, "New Omega Watch");
        when(sagaService.start(SagaOperationType.CREATE, null)).thenReturn("saga-create");
        when(watchService.create(request)).thenReturn(created);

        var result = orchestrator.create(request);

        assertThat(result.status()).isEqualTo(SagaStatus.COMPLETED);
        assertThat(result.value()).isEqualTo(created);
        InOrder order = inOrder(watchService, brandClient, sagaService);
        order.verify(watchService).create(request);
        order.verify(sagaService).attachWatch("saga-create", 11);
        order.verify(brandClient).registerWatch(2, 11);
        order.verify(sagaService).complete("saga-create");
    }

    @Test
    void createCompensatesLocalWatchWhenBrandServiceFails() {
        WatchRequest request = request(2);
        WatchResponse created = response(11, 2, "New Omega Watch");
        when(sagaService.start(SagaOperationType.CREATE, null)).thenReturn("saga-create");
        when(watchService.create(request)).thenReturn(created);
        org.mockito.Mockito.doThrow(new ResourceAccessException("brand service down"))
                .when(brandClient).registerWatch(2, 11);

        assertThatThrownBy(() -> orchestrator.create(request))
                .isInstanceOf(ApiException.class)
                .extracting(exception -> ((ApiException) exception).getStatus())
                .isEqualTo(HttpStatus.BAD_GATEWAY);

        verify(brandClient).unregisterWatch(2, 11);
        verify(watchService).deleteIfExists(11);
        verify(sagaService).markCompensated("saga-create");
    }

    @Test
    void updateRestoresWatchAndBrandWhenTransferFails() {
        WatchRequest request = request(2);
        WatchSnapshot before = new WatchSnapshot(
                10,
                "Original Rolex Watch",
                1,
                1,
                "Steel",
                "Steel",
                "100 meters",
                new BigDecimal("500.00"),
                LocalDate.now(),
                4
        );
        when(watchService.snapshot(10)).thenReturn(before);
        when(sagaService.start(SagaOperationType.UPDATE, 10)).thenReturn("saga-update");
        when(watchService.update(10, request)).thenReturn(response(10, 2, "Updated Omega Watch"));
        org.mockito.Mockito.doThrow(new ResourceAccessException("transfer failed"))
                .when(brandClient).registerWatch(2, 10);

        assertThatThrownBy(() -> orchestrator.update(10, request))
                .isInstanceOf(ApiException.class);

        verify(brandClient).unregisterWatch(2, 10);
        verify(brandClient).registerWatch(1, 10);
        verify(watchService).restore(before);
        verify(sagaService).markCompensated("saga-update");
    }

    private WatchRequest request(Integer brandId) {
        return new WatchRequest(
                "Updated Omega Watch",
                1,
                brandId,
                "Steel",
                "Leather",
                "100 meters",
                new BigDecimal("1200.00"),
                LocalDate.now(),
                10
        );
    }

    private WatchResponse response(Integer watchId, Integer brandId, String name) {
        return new WatchResponse(
                watchId,
                name,
                1,
                "Dive Watches",
                brandId,
                "Steel",
                "Leather",
                "100 meters",
                new BigDecimal("1200.00"),
                LocalDate.now(),
                10
        );
    }
}
