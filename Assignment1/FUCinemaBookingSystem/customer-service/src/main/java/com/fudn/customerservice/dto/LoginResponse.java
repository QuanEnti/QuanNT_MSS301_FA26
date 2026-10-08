package com.fudn.customerservice.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String role,
        Long userId,
        String email,
        String fullName) {
}
