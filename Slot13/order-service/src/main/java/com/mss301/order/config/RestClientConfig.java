package com.mss301.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient inventoryRestClient(
            RestClient.Builder builder,
            @Value("${services.inventory.url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }

    @Bean
    RestClient paymentRestClient(
            RestClient.Builder builder,
            @Value("${services.payment.url}") String baseUrl
    ) {
        return builder.baseUrl(baseUrl).build();
    }
}
