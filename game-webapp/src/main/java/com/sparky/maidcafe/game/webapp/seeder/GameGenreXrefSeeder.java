package com.sparky.maidcafe.game.webapp.seeder;

import com.sparky.maidcafe.game.domain.Game;
import com.sparky.maidcafe.game.domain.GameGenreXref;
import com.sparky.maidcafe.game.domain.Genre;
import com.sparky.maidcafe.game.repository.GameGenreXrefRepository;
import com.sparky.maidcafe.game.repository.GameRepository;
import com.sparky.maidcafe.game.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Component
public class GameGenreXrefSeeder implements Runnable {

    @Value("${seeding.games-genres-xref.min-count ?: 1}")
    private int gamesGenresXrefMinCount;

    @Value("${seeding.games-genres-xref.max-count ?: 3}")
    private int gamesGenresXrefMaxCount;

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreXrefRepository gameGenreXrefRepository;

    @Override
    public void run() {
        Random random = new SecureRandom();

        Iterable<Game> games = gameRepository.findAll();
        List<Genre> genres = StreamSupport.stream(genreRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        games.forEach(game -> {
            int count = random.nextInt(gamesGenresXrefMaxCount - gamesGenresXrefMinCount + 1) + gamesGenresXrefMinCount;
            Set<GameGenreXref> xrefs = new HashSet<>();

            IntStream.range(0, count).forEach(i -> {
                int genreIndex = random.nextInt(genres.size() - 1);

                GameGenreXref gameGenreXref = new GameGenreXref();
                gameGenreXref.setGameId(game.getId());
                gameGenreXref.setGenreId(genres.get(genreIndex).getId());

                if (xrefs.stream().noneMatch(xref -> xref.getGameId() == game.getId() && xref.getGenreId() == genres.get(genreIndex).getId())) {
                    xrefs.add(gameGenreXrefRepository.save(gameGenreXref));
                }
            });
        });
    }
}
