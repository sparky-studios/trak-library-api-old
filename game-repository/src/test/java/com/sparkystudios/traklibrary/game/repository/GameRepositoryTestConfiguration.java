package com.sparkystudios.traklibrary.game.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("com.sparkystudios.traklibrary.game.domain")
@EnableJpaAuditing
public class GameRepositoryTestConfiguration {
}
