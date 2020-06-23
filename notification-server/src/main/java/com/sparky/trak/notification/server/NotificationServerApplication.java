package com.sparky.trak.notification.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.notification.service",
        "com.sparky.trak.notification.server"
})
@EntityScan("com.sparky.trak.notification.domain")
@EnableJpaRepositories("com.sparky.trak.notification.repository")
public class NotificationServerApplication {

    public static void main(String... args) {
        SpringApplication.run(NotificationServerApplication.class, args);
    }
}
