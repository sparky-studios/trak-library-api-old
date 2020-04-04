package com.sparky.trak.game.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class SeederRunner implements Runnable {

    private final GameSeeder gameSeeder;
    private final GenreSeeder genreSeeder;
    private final GameGenreXrefSeeder gameGenreXrefSeeder;
    private final ConsoleSeeder consoleSeeder;
    private final GameConsoleXrefSeeder gameConsoleXrefSeeder;
    private final GameUserEntrySeeder gameUserEntrySeeder;
    private final GameRequestSeeder gameRequestSeeder;

    @Override
    public void run() {
        gameSeeder.run();
        genreSeeder.run();
        gameGenreXrefSeeder.run();
        consoleSeeder.run();
        gameConsoleXrefSeeder.run();
        gameUserEntrySeeder.run();
        gameRequestSeeder.run();
    }
}