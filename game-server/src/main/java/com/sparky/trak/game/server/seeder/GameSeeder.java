package com.sparky.trak.game.server.seeder;

import com.github.javafaker.Faker;
import com.sparky.trak.game.domain.AgeRating;
import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.dto.GameDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.util.Random;
import java.util.stream.IntStream;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class GameSeeder implements Runnable {

    @Setter
    @Value("${seeding.game.count ?: 10}")
    private int gamesCount;

    private final GameService gameService;

    @Override
    public void run() {
        Faker faker = new Faker();
        Random random = new SecureRandom();

        IntStream.range(0, gamesCount).forEach(i -> {
            GameDto gameDto = new GameDto();
            gameDto.setTitle(faker.lorem().characters(30));
            gameDto.setDescription(faker.lorem().characters(2000));
            gameDto.setReleaseDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            gameDto.setAgeRating(AgeRating.values()[random.nextInt(AgeRating.values().length)]);

            gameService.save(gameDto);
        });
    }
}
