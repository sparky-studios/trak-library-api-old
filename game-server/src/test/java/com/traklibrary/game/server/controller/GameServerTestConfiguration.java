package com.traklibrary.game.server.controller;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootConfiguration
@SpringBootApplication(scanBasePackages = {
        "com.traklibrary.game.service",
        "com.traklibrary.game.server"
})
public class GameServerTestConfiguration {
}
