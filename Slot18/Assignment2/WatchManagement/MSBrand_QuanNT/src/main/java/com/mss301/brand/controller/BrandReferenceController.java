package com.mss301.brand.controller;

import com.mss301.brand.dto.BrandWatchReferenceResponse;
import com.mss301.brand.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/brands")
public class BrandReferenceController {

    private final BrandService brandService;

    public BrandReferenceController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PutMapping("/{brandId}/watches/{watchId}")
    public BrandWatchReferenceResponse registerWatch(
            @PathVariable Integer brandId,
            @PathVariable Integer watchId
    ) {
        return brandService.registerWatch(brandId, watchId);
    }

    @DeleteMapping("/{brandId}/watches/{watchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unregisterWatch(
            @PathVariable Integer brandId,
            @PathVariable Integer watchId
    ) {
        brandService.unregisterWatch(brandId, watchId);
    }
}

