package fu.quannt.gateway;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayConfigurationTest {
    @Test
    void gatewayRoutesBothServicesAndExposesSwagger() throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            properties.load(input);
        }
        assertThat(properties.getProperty("server.port")).isEqualTo("8080");
        assertThat(properties.getProperty(
                "spring.cloud.gateway.server.webflux.routes[0].predicates[0]"))
                .isEqualTo("Path=/api/rooms/**");
        assertThat(properties.getProperty(
                "spring.cloud.gateway.server.webflux.routes[1].predicates[0]"))
                .isEqualTo("Path=/api/reservations/**");
        assertThat(properties.getProperty("springdoc.swagger-ui.urls[0].name"))
                .isEqualTo("room-service");
        assertThat(properties.getProperty("springdoc.swagger-ui.urls[1].name"))
                .isEqualTo("reservation-service");
    }
}
