package com.sparky.trak.image.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.image.service",
        "com.sparky.trak.image.server"
})
public class ImageServerApplication {

    public static void main(String... args) {
        SpringApplication.run(ImageServerApplication.class, args);
    }
}