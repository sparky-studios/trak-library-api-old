package com.sparkystudios.traklibrary.game.repository;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.sparkystudios.traklibrary.game.domain")
@EnableJpaRepositories(basePackages = {"com.sparkystudios.traklibrary.game.repository"})
@EnableJpaAuditing
public class GameRepositoryTestConfiguration {
}
