package com.sparky.trak.game.server.seeder;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class SeederRunner implements Runnable {

    private final GameSeeder gameSeeder;
    private final GenreSeeder genreSeeder;
    private final GameGenreXrefSeeder gameGenreXrefSeeder;
    private final PlatformSeeder platformSeeder;
    private final GamePlatformXrefSeeder gamePlatformXrefSeeder;
    private final GameUserEntrySeeder gameUserEntrySeeder;
    private final GameRequestSeeder gameRequestSeeder;

    @Override
    public void run() {
        gameSeeder.run();
        genreSeeder.run();
        gameGenreXrefSeeder.run();
        platformSeeder.run();
        gamePlatformXrefSeeder.run();
        gameUserEntrySeeder.run();
        gameRequestSeeder.run();
    }
}
