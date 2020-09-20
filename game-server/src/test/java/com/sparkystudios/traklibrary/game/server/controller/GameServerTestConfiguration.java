package com.sparkystudios.traklibrary.game.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.sparkystudios.traklibrary.game.service",
        "com.sparkystudios.traklibrary.game.server"
})
public class GameServerTestConfiguration {
}
