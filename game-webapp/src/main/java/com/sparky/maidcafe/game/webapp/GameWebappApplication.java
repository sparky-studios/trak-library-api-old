package com.sparky.maidcafe.game.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication(scanBasePackages = {
        "com.sparky.maidcafe.game.service",
        "com.sparky.maidcafe.game.webapp"
})
@EntityScan("com.sparky.maidcafe.game.domain")
public class GameWebappApplication {

    public static void main(String... args) {
        SpringApplication.run(GameWebappApplication.class, args);
    }
}