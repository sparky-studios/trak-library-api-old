package com.sparky.maidcafe.game.webapp.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SeederRunner implements CommandLineRunner {

    private final GameSeeder gameSeeder;
    private final GenreSeeder genreSeeder;
    private final GameGenreXrefSeeder gameGenreXrefSeeder;

    @Override
    public void run(String... args) {
        gameSeeder.run();
        genreSeeder.run();
        gameGenreXrefSeeder.run();
    }
}
