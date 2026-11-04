package com.mss301.brand.repository;

import com.mss301.brand.model.BrandWatchReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandWatchReferenceRepository extends JpaRepository<BrandWatchReference, Long> {

    Optional<BrandWatchReference> findByWatchId(Integer watchId);

    long countByBrandBrandId(Integer brandId);
}

