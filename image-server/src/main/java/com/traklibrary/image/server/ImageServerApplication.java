package com.traklibrary.image.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.image.service",
        "com.traklibrary.image.server"
})
public class ImageServerApplication {

    public static void main(String... args) {
        SpringApplication.run(ImageServerApplication.class, args);
    }
}