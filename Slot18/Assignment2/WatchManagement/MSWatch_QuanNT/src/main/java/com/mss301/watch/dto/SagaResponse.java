package com.mss301.watch.dto;

import com.mss301.watch.saga.SagaOperationType;
import com.mss301.watch.saga.SagaStatus;

import java.time.Instant;
import java.util.List;

public record SagaResponse(
        String operationId,
        SagaOperationType operationType,
        Integer watchId,
        SagaStatus status,
        String failureReason,
        Instant startedAt,
        Instant completedAt,
        List<SagaLogResponse> logs
) {
}
