package com.traklibrary.image.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.image.service",
        "com.traklibrary.image.server"
})
public class ImageServerTestConfiguration {
}
