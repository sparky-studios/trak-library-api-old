package com.sparky.trak.game.server.seeder;

import com.github.javafaker.Faker;
import com.sparky.trak.game.service.GenreService;
import com.sparky.trak.game.service.dto.GenreDto;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Profile("development")
@RequiredArgsConstructor
@Component
public class GenreSeeder implements Runnable {

    @Setter
    @Value("${seeding.genre.count ?: 10}")
    private int genreCount;

    private final GenreService genreService;

    @Override
    public void run() {
        Faker faker = new Faker();

        IntStream.range(0, genreCount).forEach(i -> {
            GenreDto genreDto = new GenreDto();
            genreDto.setName(faker.name().name());
            genreDto.setDescription(faker.lorem().characters(2000));

            genreService.save(genreDto);
        });
    }
}
