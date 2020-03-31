package com.sparky.trak.game.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableEurekaClient
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication(scanBasePackages = {
        "com.sparky.trak.game.service",
        "com.sparky.trak.game.server"
})
@EntityScan("com.sparky.trak.game.domain")
public class GameServerApplication {

    public static void main(String... args) {
        SpringApplication.run(GameServerApplication.class, args);
    }
}