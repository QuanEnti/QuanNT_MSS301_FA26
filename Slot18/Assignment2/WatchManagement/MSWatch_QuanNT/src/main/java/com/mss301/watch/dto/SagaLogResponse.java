package com.mss301.watch.dto;

import java.time.Instant;

public record SagaLogResponse(
        String step,
        String result,
        String message,
        Instant loggedAt
) {
}
