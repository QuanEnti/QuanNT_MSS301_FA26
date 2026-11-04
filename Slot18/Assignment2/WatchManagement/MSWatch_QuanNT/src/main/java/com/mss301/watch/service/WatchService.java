package com.mss301.watch.service;

import com.mss301.watch.dto.CategoryResponse;
import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.exception.ApiException;
import com.mss301.watch.model.Watch;
import com.mss301.watch.model.WatchCategory;
import com.mss301.watch.repository.WatchCategoryRepository;
import com.mss301.watch.repository.WatchRepository;
import com.mss301.watch.saga.WatchSnapshot;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchService {

    private final WatchRepository watchRepository;
    private final WatchCategoryRepository categoryRepository;

    public WatchService(WatchRepository watchRepository, WatchCategoryRepository categoryRepository) {
        this.watchRepository = watchRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<WatchResponse> findAll() {
        return watchRepository.findAllByOrderByWatchIdDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WatchResponse findById(Integer watchId) {
        return toResponse(requireWatch(watchId));
    }

    @Transactional(readOnly = true)
    public WatchSnapshot snapshot(Integer watchId) {
        return WatchSnapshot.from(toResponse(requireWatch(watchId)));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryId")).stream()
                .map(category -> new CategoryResponse(
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.getDescription(),
                        category.getMovementType(),
                        category.getPriceRange()
                ))
                .toList();
    }

    @Transactional
    public WatchResponse create(WatchRequest request) {
        WatchCategory category = requireCategory(request.categoryId());
        Watch watch = new Watch(
                request.name().trim(),
                category,
                request.brandId(),
                request.caseMaterial().trim(),
                request.strapMaterial().trim(),
                request.waterResistance().trim(),
                request.price(),
                request.releaseDate(),
                request.stock()
        );
        return toResponse(watchRepository.save(watch));
    }

    @Transactional
    public WatchResponse update(Integer watchId, WatchRequest request) {
        Watch watch = requireWatch(watchId);
        WatchCategory category = requireCategory(request.categoryId());
        watch.update(
                request.name().trim(),
                category,
                request.brandId(),
                request.caseMaterial().trim(),
                request.strapMaterial().trim(),
                request.waterResistance().trim(),
                request.price(),
                request.releaseDate(),
                request.stock()
        );
        return toResponse(watchRepository.save(watch));
    }

    @Transactional
    public void delete(Integer watchId) {
        Watch watch = requireWatch(watchId);
        watchRepository.delete(watch);
    }

    @Transactional
    public void deleteIfExists(Integer watchId) {
        watchRepository.findById(watchId).ifPresent(watchRepository::delete);
    }

    @Transactional
    public WatchResponse restore(WatchSnapshot snapshot) {
        Watch watch = requireWatch(snapshot.watchId());
        WatchCategory category = requireCategory(snapshot.categoryId());
        watch.update(
                snapshot.name(),
                category,
                snapshot.brandId(),
                snapshot.caseMaterial(),
                snapshot.strapMaterial(),
                snapshot.waterResistance(),
                snapshot.price(),
                snapshot.releaseDate(),
                snapshot.stock()
        );
        return toResponse(watchRepository.save(watch));
    }

    private Watch requireWatch(Integer watchId) {
        return watchRepository.findById(watchId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Watch %d was not found".formatted(watchId)
                ));
    }

    private WatchCategory requireCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Category %d was not found".formatted(categoryId)
                ));
    }

    private WatchResponse toResponse(Watch watch) {
        return new WatchResponse(
                watch.getWatchId(),
                watch.getName(),
                watch.getCategory().getCategoryId(),
                watch.getCategory().getCategoryName(),
                watch.getBrandId(),
                watch.getCaseMaterial(),
                watch.getStrapMaterial(),
                watch.getWaterResistance(),
                watch.getPrice(),
                watch.getReleaseDate(),
                watch.getStock()
        );
    }
}

