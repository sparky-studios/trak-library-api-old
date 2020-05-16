package com.sparky.trak.game.server.seeder;

import com.sparky.trak.game.domain.*;
import com.sparky.trak.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class GamePlatformXrefSeeder implements Runnable {

    @Setter
    @Value("${seeding.game-platform-xref.min-count ?: 1}")
    private int gamePlatformXrefMinCount;

    @Setter
    @Value("${seeding.game-platform-xref.max-count ?: 3}")
    private int gamePlatformXrefMaxCount;

    private final GameRepository gameRepository;
    private final PlatformRepository platformRepository;
    private final GamePlatformXrefRepository gamePlatformXrefRepository;

    @Override
    public void run() {
        Random random = new SecureRandom();

        Iterable<Game> games = gameRepository.findAll();
        List<Platform> platforms = StreamSupport.stream(platformRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        games.forEach(game -> {
            int count = random.nextInt(gamePlatformXrefMaxCount - gamePlatformXrefMinCount + 1) + gamePlatformXrefMinCount;
            Set<GamePlatformXref> xrefs = new HashSet<>();

            IntStream.range(0, count).forEach(i -> {
                int platformIndex = random.nextInt(platforms.size() - 1);

                GamePlatformXref gamePlatformXref = new GamePlatformXref();
                gamePlatformXref.setGameId(game.getId());
                gamePlatformXref.setPlatformId(platforms.get(platformIndex).getId());

                if (xrefs.stream().noneMatch(xref -> xref.getGameId() == game.getId() && xref.getPlatformId() == platforms.get(platformIndex).getId())) {
                    xrefs.add(gamePlatformXrefRepository.save(gamePlatformXref));
                }
            });
        });
    }
}
