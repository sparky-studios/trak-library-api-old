package com.traklibrary.authentication.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.authentication.service",
        "com.traklibrary.authentication.server"
})
public class AuthServerTestConfiguration {
}
