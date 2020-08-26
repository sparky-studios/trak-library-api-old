package com.traklibrary.notification.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.notification.service",
        "com.traklibrary.notification.server"
})
public class NotificationServerTestConfiguration {
}
