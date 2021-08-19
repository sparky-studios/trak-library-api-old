package com.sparkystudios.traklibrary.gateway.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.gateway.server",
        "com.sparkystudios.traklibrary.security.token"
})
public class GatewayServerApplication {

    public static void main(String... args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
