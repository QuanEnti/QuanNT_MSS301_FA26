package com.mss301.watch.dto;

import com.mss301.watch.validation.CurrentDate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WatchRequest(
        @NotBlank @Size(min = 11, max = 255) String name,
        @NotNull @Positive Integer categoryId,
        @NotNull @Positive Integer brandId,
        @NotBlank @Size(max = 100) String caseMaterial,
        @NotBlank @Size(max = 100) String strapMaterial,
        @NotBlank @Size(max = 50) String waterResistance,
        @NotNull @DecimalMin(value = "100.00") BigDecimal price,
        @NotNull @CurrentDate LocalDate releaseDate,
        @NotNull @PositiveOrZero Integer stock
) {
}

