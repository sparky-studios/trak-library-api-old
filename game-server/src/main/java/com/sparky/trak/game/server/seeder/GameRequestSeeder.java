package com.sparky.trak.game.server.seeder;

import com.github.javafaker.Faker;
import com.sparky.trak.game.service.GameRequestService;
import com.sparky.trak.game.service.dto.GameRequestDto;
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
public class GameRequestSeeder implements Runnable {

    @Setter
    @Value("${seeding.game-request.count ?: 10}")
    private int gameRequestCount;

    private final GameRequestService gameRequestService;

    @Override
    public void run() {
        Random random = new SecureRandom();
        Faker faker = new Faker();

        IntStream.range(0, gameRequestCount).forEach(i -> {
            GameRequestDto gameRequestDto = new GameRequestDto();
            gameRequestDto.setTitle(faker.book().title());

            boolean completed = random.nextBoolean();
            if (completed) {
                gameRequestDto.setCompleted(true);
                gameRequestDto.setCompletedDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } else {
                gameRequestDto.setCompleted(false);
            }
            gameRequestDto.setUserId(1L);

            gameRequestService.save(gameRequestDto);
        });
    }
}
