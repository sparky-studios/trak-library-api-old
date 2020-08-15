package com.traklibrary.game.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.game.service",
        "com.traklibrary.game.server"
})
@EntityScan("com.traklibrary.game.domain")
public class GameServerApplication {

    public static void main(String... args) {
        SpringApplication.run(GameServerApplication.class, args);
    }
}