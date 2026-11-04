package com.mss301.brand.dto;

import java.time.Instant;

public record BrandWatchReferenceResponse(
        Integer watchId,
        Integer brandId,
        Instant registeredAt
) {
}

