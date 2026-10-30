package fu.quannt.post.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Jackson 2 mapper used to convert the generic data returned by OpenFeign. */
@Configuration
public class HttpClientConfig {

    @Bean
    public ObjectMapper feignObjectMapper() {
        return new ObjectMapper();
    }
}
