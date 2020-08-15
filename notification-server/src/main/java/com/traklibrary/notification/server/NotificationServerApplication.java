package com.traklibrary.notification.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.notification.service",
        "com.traklibrary.notification.server"
})
@EntityScan("com.traklibrary.notification.domain")
@EnableJpaRepositories("com.traklibrary.notification.repository")
public class NotificationServerApplication {

    public static void main(String... args) {
        SpringApplication.run(NotificationServerApplication.class, args);
    }
}
