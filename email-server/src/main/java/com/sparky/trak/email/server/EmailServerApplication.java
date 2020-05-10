package com.sparky.trak.email.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.email.service",
        "com.sparky.trak.email.server"
})
public class EmailServerApplication {

    public static void main(String... args) {
        SpringApplication.run(EmailServerApplication.class, args);
    }
}
