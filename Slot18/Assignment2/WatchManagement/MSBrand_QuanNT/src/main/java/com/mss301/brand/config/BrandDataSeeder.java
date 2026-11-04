package com.mss301.brand.config;

import com.mss301.brand.model.BrandWatchReference;
import com.mss301.brand.model.WatchBrand;
import com.mss301.brand.repository.BrandWatchReferenceRepository;
import com.mss301.brand.repository.WatchBrandRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class BrandDataSeeder {

    @Bean
    CommandLineRunner seedBrands(
            WatchBrandRepository repository,
            BrandWatchReferenceRepository referenceRepository
    ) {
        return args -> {
            List<WatchBrand> brands;
            if (repository.count() == 0) {
                brands = repository.saveAll(List.of(
                        new WatchBrand("Rolex", "Switzerland"),
                        new WatchBrand("Omega", "Switzerland"),
                        new WatchBrand("Seiko", "Japan"),
                        new WatchBrand("Casio", "Japan"),
                        new WatchBrand("TAG Heuer", "Switzerland")
                ));
            } else {
                brands = repository.findAll(Sort.by(Sort.Direction.ASC, "brandId"));
            }
            if (brands.size() < 5) {
                throw new IllegalStateException("The five required watch brands are not available");
            }

            int[] brandIndexes = {0, 1, 2, 3, 4, 1, 2, 0, 3, 3};
            for (int index = 0; index < brandIndexes.length; index++) {
                int watchId = index + 1;
                if (referenceRepository.findByWatchId(watchId).isEmpty()) {
                    referenceRepository.save(new BrandWatchReference(watchId, brands.get(brandIndexes[index])));
                }
            }
        };
    }
}

