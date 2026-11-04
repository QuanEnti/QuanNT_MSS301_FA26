package com.mss301.watch.service;

import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.exception.ApiException;
import com.mss301.watch.model.Watch;
import com.mss301.watch.model.WatchCategory;
import com.mss301.watch.repository.WatchCategoryRepository;
import com.mss301.watch.repository.WatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WatchServiceTest {

    @Mock
    private WatchRepository watchRepository;

    @Mock
    private WatchCategoryRepository categoryRepository;

    @InjectMocks
    private WatchService watchService;

    @Test
    void createMapsCategoryAndPersistsWatch() {
        WatchCategory category = category();
        WatchRequest request = request(1);
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(watchRepository.save(any(Watch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = watchService.create(request);

        assertThat(response.categoryName()).isEqualTo("Dive Watches");
        assertThat(response.brandId()).isEqualTo(1);
        verify(watchRepository).save(any(Watch.class));
    }

    @Test
    void updateChangesExistingWatchWithoutCreatingAnother() {
        WatchCategory category = category();
        Watch existing = new Watch("Original Watch", category, 1, "Steel", "Steel", "100 meters", new BigDecimal("500"), LocalDate.now(), 1);
        when(watchRepository.findById(10)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(watchRepository.save(existing)).thenReturn(existing);

        var response = watchService.update(10, request(2));

        assertThat(response.name()).isEqualTo("Updated Watch Name");
        assertThat(response.brandId()).isEqualTo(2);
        verify(watchRepository).save(existing);
    }

    @Test
    void deleteRejectsUnknownWatch() {
        when(watchRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> watchService.delete(99))
                .isInstanceOf(ApiException.class)
                .hasMessage("Watch 99 was not found");
    }

    private WatchRequest request(Integer brandId) {
        return new WatchRequest(
                "Updated Watch Name",
                1,
                brandId,
                "Steel",
                "Leather",
                "100 meters",
                new BigDecimal("1200.00"),
                LocalDate.now(),
                10
        );
    }

    private WatchCategory category() {
        return new WatchCategory(1, "Dive Watches", "Dive", "Automatic", "$500 - $10,000");
    }
}

