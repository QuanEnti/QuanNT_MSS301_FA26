package com.mss301.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration(proxyBeanMethods = false)
public class GatewayRoutes {

    @Bean
    RouterFunction<ServerResponse> accountRoute(
            @Value("${services.account.url:http://localhost:8081}") String accountUrl
    ) {
        return route("account_service")
                .route(RequestPredicates.path("/api/auth/**"), http())
                .before(uri(accountUrl))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> brandRoute(
            @Value("${services.brand.url:http://localhost:8082}") String brandUrl
    ) {
        return route("brand_service")
                .route(RequestPredicates.path("/api/brands/**"), http())
                .before(uri(brandUrl))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> watchRoute(
            @Value("${services.watch.url:http://localhost:8083}") String watchUrl
    ) {
        return route("watch_service")
                .route(RequestPredicates.path("/api/watches/**"), http())
                .before(uri(watchUrl))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> categoryRoute(
            @Value("${services.watch.url:http://localhost:8083}") String watchUrl
    ) {
        return route("category_service")
                .route(RequestPredicates.path("/api/categories/**"), http())
                .before(uri(watchUrl))
                .build();
    }

    @Bean
    RouterFunction<ServerResponse> sagaRoute(
            @Value("${services.watch.url:http://localhost:8083}") String watchUrl
    ) {
        return route("saga_service")
                .route(RequestPredicates.path("/api/sagas/**"), http())
                .before(uri(watchUrl))
                .build();
    }
}
