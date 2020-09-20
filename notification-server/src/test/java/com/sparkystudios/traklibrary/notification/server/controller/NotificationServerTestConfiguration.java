package com.sparkystudios.traklibrary.notification.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.notification.service",
        "com.sparkystudios.traklibrary.notification.server"
})
public class NotificationServerTestConfiguration {
}
