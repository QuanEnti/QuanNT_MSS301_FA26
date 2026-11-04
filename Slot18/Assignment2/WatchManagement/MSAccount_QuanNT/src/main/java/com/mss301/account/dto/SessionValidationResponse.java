package com.mss301.account.dto;

public record SessionValidationResponse(
        boolean valid,
        Integer accountId,
        String username,
        Integer role
) {

    public static SessionValidationResponse invalid() {
        return new SessionValidationResponse(false, null, null, null);
    }
}

