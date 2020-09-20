package com.sparkystudios.traklibrary.notification.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.sparkystudios.traklibrary.notification.domain")
public class NotificationRepositoryTestConfiguration {
}
