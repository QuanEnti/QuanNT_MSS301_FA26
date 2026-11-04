package com.mss301.account.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        Integer accountId,
        String username,
        String email,
        Integer role
) {
}

