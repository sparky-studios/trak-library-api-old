package com.traklibrary.authentication.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.authentication.service",
        "com.traklibrary.authentication.server"
})
@EntityScan("com.traklibrary.authentication.domain")
@EnableJpaRepositories("com.traklibrary.authentication.repository")
public class AuthServerApplication {

    public static void main(String... args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
