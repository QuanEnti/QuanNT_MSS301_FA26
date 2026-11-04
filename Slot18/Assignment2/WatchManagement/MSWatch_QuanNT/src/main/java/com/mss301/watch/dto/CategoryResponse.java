package com.mss301.watch.dto;

public record CategoryResponse(
        Integer categoryId,
        String categoryName,
        String description,
        String movementType,
        String priceRange
) {
}

