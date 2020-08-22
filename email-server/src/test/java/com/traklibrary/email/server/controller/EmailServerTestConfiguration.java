package com.traklibrary.email.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.email.service",
        "com.traklibrary.email.server"
})
public class EmailServerTestConfiguration {
}
