package com.sparky.trak.authentication.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableEurekaClient
@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.authentication.service",
        "com.sparky.trak.authentication.server"
})
@EntityScan("com.sparky.trak.authentication.domain")
@EnableJpaRepositories("com.sparky.trak.authentication.repository")
public class AuthServerApplication {

    public static void main(String... args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }
}
