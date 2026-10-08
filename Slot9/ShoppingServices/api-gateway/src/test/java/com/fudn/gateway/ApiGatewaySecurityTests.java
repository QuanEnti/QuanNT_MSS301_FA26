package com.fudn.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock({
        @ConfigureWireMock(port = 8080, name = "product-service"),
        @ConfigureWireMock(port = 8081, name = "order-service"),
        @ConfigureWireMock(port = 8082, name = "inventory-service")
})
class ApiGatewaySecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void requestWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists("WWW-Authenticate"));
    }

    @Test
    void requestWithValidJwtShouldBeRoutedToProductService() throws Exception {
        stubFor(get(urlEqualTo("/api/products"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"iPhone 15\"}]")));

        mockMvc.perform(get("/api/products").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"iPhone 15\"}]"));

        verify(getRequestedFor(urlEqualTo("/api/products")));
    }

    @Test
    void postOrderWithValidJwtShouldBeRoutedToOrderService() throws Exception {
        stubFor(post(urlEqualTo("/api/order"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody("Order Placed Successfully")));

        mockMvc.perform(post("/api/order")
                        .with(jwt())
                        .contentType("application/json")
                        .content("{\"skuCode\":\"iphone_15\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order Placed Successfully"));

        verify(postRequestedFor(urlEqualTo("/api/order")));
    }

    @Test
    void inventoryRouteShouldForwardQueryParams() throws Exception {
        stubFor(get(urlEqualTo("/api/inventory?skuCode=iphone_15&quantity=1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("true")));

        mockMvc.perform(get("/api/inventory?skuCode=iphone_15&quantity=1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
