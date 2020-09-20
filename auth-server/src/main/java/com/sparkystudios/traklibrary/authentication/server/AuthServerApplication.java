package com.sparkystudios.traklibrary.authentication.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.security",
        "com.sparkystudios.traklibrary.authentication.service",
        "com.sparkystudios.traklibrary.authentication.server"
})
@EntityScan("com.sparkystudios.traklibrary.authentication.domain")
@EnableJpaRepositories("com.sparkystudios.traklibrary.authentication.repository")
public class AuthServerApplication {

    public static void main(String... args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
