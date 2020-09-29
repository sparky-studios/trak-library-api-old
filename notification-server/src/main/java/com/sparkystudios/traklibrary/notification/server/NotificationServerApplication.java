package com.sparkystudios.traklibrary.notification.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.notification.service",
        "com.sparkystudios.traklibrary.notification.server",
        "com.sparkystudios.traklibrary.security"
})
@EntityScan("com.sparkystudios.traklibrary.notification.domain")
@EnableJpaRepositories("com.sparkystudios.traklibrary.notification.repository")
@EnableJpaAuditing
public class NotificationServerApplication {

    public static void main(String... args) {
        SpringApplication.run(NotificationServerApplication.class, args);
    }
}
