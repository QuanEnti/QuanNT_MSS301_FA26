package com.mss301.watch.service;

import com.mss301.watch.client.BrandClient;
import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.exception.ApiException;
import com.mss301.watch.saga.SagaOperationType;
import com.mss301.watch.saga.SagaResult;
import com.mss301.watch.saga.SagaStatus;
import com.mss301.watch.saga.WatchSnapshot;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class WatchSagaOrchestrator {

    private static final int MAX_COMPENSATION_ATTEMPTS = 3;

    private final WatchService watchService;
    private final BrandClient brandClient;
    private final SagaService sagaService;

    public WatchSagaOrchestrator(
            WatchService watchService,
            BrandClient brandClient,
            SagaService sagaService
    ) {
        this.watchService = watchService;
        this.brandClient = brandClient;
        this.sagaService = sagaService;
    }

    public SagaResult<WatchResponse> create(WatchRequest request) {
        String operationId = sagaService.start(SagaOperationType.CREATE, null);
        WatchResponse created = null;
        try {
            created = watchService.create(request);
            sagaService.attachWatch(operationId, created.watchId());
            sagaService.log(operationId, "CREATE_WATCH", "SUCCESS", "Watch created in DBWatch");

            brandClient.registerWatch(created.brandId(), created.watchId());
            sagaService.log(operationId, "REGISTER_BRAND", "SUCCESS", "Brand reference registered in DBBrand");
        } catch (RuntimeException exception) {
            compensateCreate(operationId, created, exception);
        }

        sagaService.complete(operationId);
        return new SagaResult<>(operationId, SagaStatus.COMPLETED, created);
    }

    public SagaResult<WatchResponse> update(Integer watchId, WatchRequest request) {
        WatchSnapshot before = watchService.snapshot(watchId);
        String operationId = sagaService.start(SagaOperationType.UPDATE, watchId);
        WatchResponse updated = null;
        try {
            updated = watchService.update(watchId, request);
            sagaService.log(operationId, "UPDATE_WATCH", "SUCCESS", "Watch updated in DBWatch");

            brandClient.registerWatch(updated.brandId(), watchId);
            sagaService.log(operationId, "REGISTER_BRAND", "SUCCESS", "New brand reference registered");

            if (!before.brandId().equals(updated.brandId())) {
                brandClient.unregisterWatch(before.brandId(), watchId);
                sagaService.log(operationId, "UNREGISTER_OLD_BRAND", "SUCCESS", "Old brand reference removed");
            }
        } catch (RuntimeException exception) {
            compensateUpdate(operationId, before, request.brandId(), exception);
        }

        sagaService.complete(operationId);
        return new SagaResult<>(operationId, SagaStatus.COMPLETED, updated);
    }

    public SagaResult<Void> delete(Integer watchId) {
        WatchSnapshot before = watchService.snapshot(watchId);
        String operationId = sagaService.start(SagaOperationType.DELETE, watchId);
        try {
            brandClient.unregisterWatch(before.brandId(), watchId);
            sagaService.log(operationId, "UNREGISTER_BRAND", "SUCCESS", "Brand reference removed");

            watchService.delete(watchId);
            sagaService.log(operationId, "DELETE_WATCH", "SUCCESS", "Watch deleted from DBWatch");
        } catch (RuntimeException exception) {
            compensateDelete(operationId, before, exception);
        }

        sagaService.complete(operationId);
        return new SagaResult<>(operationId, SagaStatus.COMPLETED, null);
    }

    private void compensateCreate(String operationId, WatchResponse created, RuntimeException cause) {
        beginCompensation(operationId, cause);
        if (created != null) {
            Integer watchId = created.watchId();
            retry(operationId, "UNREGISTER_BRAND", () -> brandClient.unregisterWatch(created.brandId(), watchId));
            retry(operationId, "DELETE_WATCH", () -> watchService.deleteIfExists(watchId));
        }
        finishCompensation(operationId, cause);
    }

    private void compensateUpdate(
            String operationId,
            WatchSnapshot before,
            Integer attemptedBrandId,
            RuntimeException cause
    ) {
        beginCompensation(operationId, cause);
        if (!before.brandId().equals(attemptedBrandId)) {
            retry(operationId, "UNREGISTER_NEW_BRAND", () -> brandClient.unregisterWatch(attemptedBrandId, before.watchId()));
            retry(operationId, "RESTORE_OLD_BRAND", () -> brandClient.registerWatch(before.brandId(), before.watchId()));
        }
        retry(operationId, "RESTORE_WATCH", () -> watchService.restore(before));
        finishCompensation(operationId, cause);
    }

    private void compensateDelete(String operationId, WatchSnapshot before, RuntimeException cause) {
        beginCompensation(operationId, cause);
        retry(operationId, "RESTORE_BRAND", () -> brandClient.registerWatch(before.brandId(), before.watchId()));
        finishCompensation(operationId, cause);
    }

    private void beginCompensation(String operationId, RuntimeException cause) {
        sagaService.fail(operationId, rootMessage(cause));
        sagaService.markCompensating(operationId);
    }

    private void finishCompensation(String operationId, RuntimeException cause) {
        sagaService.markCompensated(operationId);
        throw translate(cause);
    }

    private void retry(String operationId, String step, Runnable compensation) {
        RuntimeException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_COMPENSATION_ATTEMPTS; attempt++) {
            try {
                compensation.run();
                sagaService.log(operationId, step, "COMPENSATED", "Compensation completed on attempt " + attempt);
                return;
            } catch (RuntimeException exception) {
                lastFailure = exception;
                sagaService.log(operationId, step, "RETRY", "Compensation attempt " + attempt + " failed");
            }
        }
        throw new ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Saga compensation failed after %d attempts: %s"
                        .formatted(MAX_COMPENSATION_ATTEMPTS, rootMessage(lastFailure))
        );
    }

    private RuntimeException translate(RuntimeException cause) {
        if (cause instanceof ApiException) {
            return cause;
        }
        if (cause instanceof RestClientException) {
            return new ApiException(HttpStatus.BAD_GATEWAY, "Brand Service synchronization failed: " + rootMessage(cause));
        }
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, rootMessage(cause));
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.getClass().getSimpleName() : current.getMessage();
    }
}
