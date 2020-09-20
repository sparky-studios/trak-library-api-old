package com.sparkystudios.traklibrary.image.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.image.service",
        "com.sparkystudios.traklibrary.image.server"
})
public class ImageServerTestConfiguration {
}
