package com.mss301.brand.service;

import com.mss301.brand.dto.BrandResponse;
import com.mss301.brand.dto.BrandWatchReferenceResponse;
import com.mss301.brand.exception.ApiException;
import com.mss301.brand.model.BrandWatchReference;
import com.mss301.brand.model.WatchBrand;
import com.mss301.brand.repository.BrandWatchReferenceRepository;
import com.mss301.brand.repository.WatchBrandRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandService {

    private final WatchBrandRepository brandRepository;
    private final BrandWatchReferenceRepository referenceRepository;

    public BrandService(
            WatchBrandRepository brandRepository,
            BrandWatchReferenceRepository referenceRepository
    ) {
        this.brandRepository = brandRepository;
        this.referenceRepository = referenceRepository;
    }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brandRepository.findAll(Sort.by(Sort.Direction.ASC, "brandId"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse findById(Integer brandId) {
        return toResponse(requireBrand(brandId));
    }

    @Transactional
    public BrandWatchReferenceResponse registerWatch(Integer brandId, Integer watchId) {
        WatchBrand brand = requireBrand(brandId);
        BrandWatchReference reference = referenceRepository.findByWatchId(watchId)
                .map(existing -> {
                    if (!existing.getBrand().getBrandId().equals(brandId)) {
                        existing.changeBrand(brand);
                        return referenceRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> referenceRepository.save(new BrandWatchReference(watchId, brand)));
        return toReferenceResponse(reference);
    }

    @Transactional
    public void unregisterWatch(Integer brandId, Integer watchId) {
        referenceRepository.findByWatchId(watchId)
                .filter(reference -> reference.getBrand().getBrandId().equals(brandId))
                .ifPresent(referenceRepository::delete);
    }

    private WatchBrand requireBrand(Integer brandId) {
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Brand %d was not found".formatted(brandId)
                ));
    }

    private BrandResponse toResponse(WatchBrand brand) {
        return new BrandResponse(
                brand.getBrandId(),
                brand.getBrandName(),
                brand.getCountryOfOrigin(),
                referenceRepository.countByBrandBrandId(brand.getBrandId())
        );
    }

    private BrandWatchReferenceResponse toReferenceResponse(BrandWatchReference reference) {
        return new BrandWatchReferenceResponse(
                reference.getWatchId(),
                reference.getBrand().getBrandId(),
                reference.getRegisteredAt()
        );
    }
}

