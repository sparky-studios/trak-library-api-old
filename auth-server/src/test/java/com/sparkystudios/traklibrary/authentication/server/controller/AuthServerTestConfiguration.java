package com.sparkystudios.traklibrary.authentication.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.authentication.service",
        "com.sparkystudios.traklibrary.authentication.server"
})
public class AuthServerTestConfiguration {
}
