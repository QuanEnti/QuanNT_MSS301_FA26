package com.mss301.gateway.client;

public record SessionValidationResponse(
        boolean valid,
        Integer accountId,
        String username,
        Integer role
) {
}
