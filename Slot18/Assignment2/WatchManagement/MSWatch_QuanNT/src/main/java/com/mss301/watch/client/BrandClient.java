package com.mss301.watch.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BrandClient {

    private final RestClient restClient;

    public BrandClient(
            RestClient.Builder builder,
            @Value("${services.brand.url:http://localhost:8082}") String brandServiceUrl
    ) {
        this.restClient = builder.baseUrl(brandServiceUrl).build();
    }

    public void registerWatch(Integer brandId, Integer watchId) {
        restClient.put()
                .uri("/api/internal/brands/{brandId}/watches/{watchId}", brandId, watchId)
                .retrieve()
                .toBodilessEntity();
    }

    public void unregisterWatch(Integer brandId, Integer watchId) {
        restClient.delete()
                .uri("/api/internal/brands/{brandId}/watches/{watchId}", brandId, watchId)
                .retrieve()
                .toBodilessEntity();
    }
}
