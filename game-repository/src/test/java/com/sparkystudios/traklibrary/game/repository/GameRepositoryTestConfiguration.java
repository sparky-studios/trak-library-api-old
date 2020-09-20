package com.sparkystudios.traklibrary.game.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.sparkystudios.traklibrary.game.domain")
public class GameRepositoryTestConfiguration {
}
