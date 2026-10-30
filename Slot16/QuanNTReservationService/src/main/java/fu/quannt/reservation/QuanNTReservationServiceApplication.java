package fu.quannt.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class QuanNTReservationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuanNTReservationServiceApplication.class, args);
    }
}
