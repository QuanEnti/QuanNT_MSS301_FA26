package com.mss301.brand.service;

import com.mss301.brand.dto.BrandWatchReferenceResponse;
import com.mss301.brand.model.BrandWatchReference;
import com.mss301.brand.model.WatchBrand;
import com.mss301.brand.repository.BrandWatchReferenceRepository;
import com.mss301.brand.repository.WatchBrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private WatchBrandRepository brandRepository;

    @Mock
    private BrandWatchReferenceRepository referenceRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void registerWatchCreatesReference() {
        WatchBrand rolex = new WatchBrand(1, "Rolex", "Switzerland");
        BrandWatchReference reference = new BrandWatchReference(10, rolex);
        when(brandRepository.findById(1)).thenReturn(Optional.of(rolex));
        when(referenceRepository.findByWatchId(10)).thenReturn(Optional.empty());
        when(referenceRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(reference);

        BrandWatchReferenceResponse response = brandService.registerWatch(1, 10);

        assertThat(response.brandId()).isEqualTo(1);
        assertThat(response.watchId()).isEqualTo(10);
    }

    @Test
    void registerWatchIsIdempotentForSameBrand() {
        WatchBrand rolex = new WatchBrand(1, "Rolex", "Switzerland");
        BrandWatchReference reference = new BrandWatchReference(10, rolex);
        when(brandRepository.findById(1)).thenReturn(Optional.of(rolex));
        when(referenceRepository.findByWatchId(10)).thenReturn(Optional.of(reference));

        brandService.registerWatch(1, 10);

        verify(referenceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void registerWatchTransfersReferenceToAnotherBrand() {
        WatchBrand rolex = new WatchBrand(1, "Rolex", "Switzerland");
        WatchBrand omega = new WatchBrand(2, "Omega", "Switzerland");
        when(brandRepository.findById(2)).thenReturn(Optional.of(omega));
        when(referenceRepository.findByWatchId(10))
                .thenReturn(Optional.of(new BrandWatchReference(10, rolex)));
        when(referenceRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BrandWatchReferenceResponse response = brandService.registerWatch(2, 10);

        assertThat(response.brandId()).isEqualTo(2);
        verify(referenceRepository).save(org.mockito.ArgumentMatchers.any());
    }
}

