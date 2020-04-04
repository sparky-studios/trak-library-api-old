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
public class GameConsoleXrefSeeder implements Runnable {

    @Setter
    @Value("${seeding.game-console-xref.min-count ?: 1}")
    private int gameConsoleXrefMinCount;

    @Setter
    @Value("${seeding.game-console-xref.max-count ?: 3}")
    private int gameConsoleXrefMaxCount;

    private final GameRepository gameRepository;
    private final ConsoleRepository consoleRepository;
    private final GameConsoleXrefRepository gameConsoleXrefRepository;

    @Override
    public void run() {
        Random random = new SecureRandom();

        Iterable<Game> games = gameRepository.findAll();
        List<Console> consoles = StreamSupport.stream(consoleRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        games.forEach(game -> {
            int count = random.nextInt(gameConsoleXrefMaxCount - gameConsoleXrefMinCount + 1) + gameConsoleXrefMinCount;
            Set<GameConsoleXref> xrefs = new HashSet<>();

            IntStream.range(0, count).forEach(i -> {
                int consoleIndex = random.nextInt(consoles.size() - 1);

                GameConsoleXref gameConsoleXref = new GameConsoleXref();
                gameConsoleXref.setGameId(game.getId());
                gameConsoleXref.setConsoleId(consoles.get(consoleIndex).getId());

                if (xrefs.stream().noneMatch(xref -> xref.getGameId() == game.getId() && xref.getConsoleId() == consoles.get(consoleIndex).getId())) {
                    xrefs.add(gameConsoleXrefRepository.save(gameConsoleXref));
                }
            });
        });
    }
}
