package com.fudn.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @EnableFeignClients — bắt buộc để Spring scan và sinh proxy cho
 * các interface có @FeignClient trong package com.fudn.orderservice.
 * Nếu quên annotation này → NoSuchBeanDefinitionException khi inject InventoryClient.
 */
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
