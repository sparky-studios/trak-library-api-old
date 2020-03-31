package com.sparky.trak.game.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SeederRunner implements CommandLineRunner {

    private final GameSeeder gameSeeder;
    private final GenreSeeder genreSeeder;
    private final GameGenreXrefSeeder gameGenreXrefSeeder;
    private final ConsoleSeeder consoleSeeder;
    private final GameConsoleXrefSeeder gameConsoleXrefSeeder;
    private final GameUserEntrySeeder gameUserEntrySeeder;

    @Override
    public void run(String... args) {
        gameSeeder.run();
        genreSeeder.run();
        gameGenreXrefSeeder.run();
        consoleSeeder.run();
        gameConsoleXrefSeeder.run();
        gameUserEntrySeeder.run();
    }
}
