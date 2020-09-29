package com.sparkystudios.traklibrary.notification.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.sparkystudios.traklibrary.notification.domain")
@EnableJpaAuditing
public class NotificationRepositoryTestConfiguration {
}
