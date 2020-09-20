package com.sparkystudios.traklibrary.email.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.email.service",
        "com.sparkystudios.traklibrary.email.server"
})
public class EmailServerTestConfiguration {
}
