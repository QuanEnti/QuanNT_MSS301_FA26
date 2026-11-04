package com.mss301.account.config;

import com.mss301.account.model.StoreAccount;
import com.mss301.account.repository.StoreAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class AccountDataSeeder {

    @Bean
    CommandLineRunner seedAccounts(StoreAccountRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    new StoreAccount("adminsys", "admin@drawingpen.com.vn", "@1", 1, true),
                    new StoreAccount("staff02", "staff02@drawingpen.com.vn", "@1", 2, true),
                    new StoreAccount("member01", "member01@drawingpen.com.vn", "@1", 3, true),
                    new StoreAccount("member02", "member02@drawingpen.com.vn", "@1", 3, false),
                    new StoreAccount("staff01", "staff01@drawingpen.com.vn", "@1", 2, false)
            ));
        };
    }
}

