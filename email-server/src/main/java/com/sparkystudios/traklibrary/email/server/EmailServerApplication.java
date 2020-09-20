package com.sparkystudios.traklibrary.email.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.email.service",
        "com.sparkystudios.traklibrary.email.server"
})
public class EmailServerApplication {

    public static void main(String... args) {
        SpringApplication.run(EmailServerApplication.class, args);
    }
}
