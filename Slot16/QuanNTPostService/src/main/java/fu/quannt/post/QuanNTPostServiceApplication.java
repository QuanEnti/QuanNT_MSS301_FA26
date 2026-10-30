package fu.quannt.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QuanNTPostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuanNTPostServiceApplication.class, args);
    }
}
