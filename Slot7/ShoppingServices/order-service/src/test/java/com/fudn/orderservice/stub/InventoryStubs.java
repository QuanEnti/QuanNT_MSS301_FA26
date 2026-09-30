package com.fudn.orderservice.stub;

import lombok.experimental.UtilityClass;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * WireMock stubs cho Inventory Service.
 *
 * Dùng withQueryParam() thay vì urlEqualTo() để tránh lỗi thứ tự query params.
 * Nếu Feign gửi sai params → stub không match → 404 → test fail
 * → phát hiện lỗi config Feign ngay trong test.
 */
@UtilityClass
public class InventoryStubs {

    /**
     * Stub: GET /api/inventory?skuCode=xxx&quantity=N → 200 + body "true"
     * (Inventory Service báo đủ hàng)
     */
    public void stubInventoryCall(String skuCode, Integer quantity) {
        stubFor(get(urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", equalTo(skuCode))
                .withQueryParam("quantity", equalTo(quantity.toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("true")));
    }

    /**
     * Stub: GET /api/inventory?skuCode=xxx&quantity=N → 200 + body "false"
     * (Inventory Service báo hết hàng)
     */
    public void stubInventoryOutOfStock(String skuCode, Integer quantity) {
        stubFor(get(urlPathEqualTo("/api/inventory"))
                .withQueryParam("skuCode", equalTo(skuCode))
                .withQueryParam("quantity", equalTo(quantity.toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("false")));
    }
}
