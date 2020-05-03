package com.sparky.trak.email.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.email.service",
        "com.sparky.trak.email.server"
})
@EntityScan("com.sparky.trak.authentication.domain")
public class EmailServerApplication {

    public static void main(String... args) {
        SpringApplication.run(EmailServerApplication.class, args);
    }
}
