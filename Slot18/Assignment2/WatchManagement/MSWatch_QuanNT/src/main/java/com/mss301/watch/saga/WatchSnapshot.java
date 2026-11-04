package com.mss301.watch.saga;

import com.mss301.watch.dto.WatchResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WatchSnapshot(
        Integer watchId,
        String name,
        Integer categoryId,
        Integer brandId,
        String caseMaterial,
        String strapMaterial,
        String waterResistance,
        BigDecimal price,
        LocalDate releaseDate,
        Integer stock
) {

    public static WatchSnapshot from(WatchResponse watch) {
        return new WatchSnapshot(
                watch.watchId(),
                watch.name(),
                watch.categoryId(),
                watch.brandId(),
                watch.caseMaterial(),
                watch.strapMaterial(),
                watch.waterResistance(),
                watch.price(),
                watch.releaseDate(),
                watch.stock()
        );
    }
}
