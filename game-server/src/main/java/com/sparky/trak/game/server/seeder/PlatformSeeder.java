package com.sparky.trak.game.server.seeder;

import com.github.javafaker.Faker;
import com.sparky.trak.game.service.PlatformService;
import com.sparky.trak.game.service.dto.PlatformDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.stream.IntStream;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@Component
public class PlatformSeeder implements Runnable {

    @Setter
    @Value("${seeding.platform.count ?: 10}")
    private int platformCount;

    private final PlatformService platformService;

    @Override
    public void run() {
        Faker faker = new Faker();

        IntStream.range(0, platformCount).forEach(i -> {
            PlatformDto platformDto = new PlatformDto();
            platformDto.setName(faker.lorem().characters(30));
            platformDto.setDescription(faker.lorem().characters(2000));
            platformDto.setReleaseDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            platformService.save(platformDto);
        });
    }
}
