package fu.quannt.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayConfigurationTest {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    @Autowired
    private CorsWebFilter corsWebFilter;

    @Test
    void configuresBothServiceRoutesAndCors() {
        List<String> routeIds = routeDefinitionLocator.getRouteDefinitions()
                .map(RouteDefinition::getId)
                .collectList()
                .block();

        assertNotNull(routeIds);
        assertTrue(routeIds.contains("user-service"));
        assertTrue(routeIds.contains("post-service"));
        assertNotNull(corsWebFilter);
    }
}
