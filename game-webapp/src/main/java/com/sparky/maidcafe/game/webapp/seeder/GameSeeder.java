package com.sparky.maidcafe.game.webapp.seeder;

import com.github.javafaker.Faker;
import com.sparky.maidcafe.game.domain.AgeRating;
import com.sparky.maidcafe.game.service.GameService;
import com.sparky.maidcafe.game.service.dto.GameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.util.Random;

@RequiredArgsConstructor
@Component
public class GameSeeder implements Runnable {

    private final GameService gameService;

    @Override
    public void run() {
        Faker faker = new Faker();
        Random random = new SecureRandom();

        GameDto gameDto = new GameDto();
        gameDto.setTitle(faker.book().title());
        gameDto.setDescription(faker.lorem().characters(2000));
        gameDto.setReleaseDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        gameDto.setAgeRating(AgeRating.values()[random.nextInt(AgeRating.values().length)]);

        gameService.save(gameDto);
    }
}
