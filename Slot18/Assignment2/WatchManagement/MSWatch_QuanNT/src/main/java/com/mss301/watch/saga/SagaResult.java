package com.mss301.watch.saga;

public record SagaResult<T>(
        String operationId,
        SagaStatus status,
        T value
) {
}
