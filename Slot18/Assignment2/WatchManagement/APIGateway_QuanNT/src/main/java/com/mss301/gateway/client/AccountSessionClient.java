package com.mss301.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AccountSessionClient {

    private final RestClient restClient;

    public AccountSessionClient(
            RestClient.Builder builder,
            @Value("${services.account.url:http://localhost:8081}") String accountServiceUrl
    ) {
        this.restClient = builder.baseUrl(accountServiceUrl).build();
    }

    public SessionValidationResponse validate(String token) {
        SessionValidationResponse response = restClient.get()
                .uri("/api/internal/sessions/{token}", token)
                .retrieve()
                .body(SessionValidationResponse.class);
        return response == null
                ? new SessionValidationResponse(false, null, null, null)
                : response;
    }
}
