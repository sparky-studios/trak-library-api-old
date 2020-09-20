package com.sparkystudios.traklibrary.image.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.image.service",
        "com.sparkystudios.traklibrary.image.server",
        "com.sparkystudios.traklibrary.security"
})
public class ImageServerApplication {

    public static void main(String... args) {
        SpringApplication.run(ImageServerApplication.class, args);
    }
}