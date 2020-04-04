package com.sparky.trak.game.server.seeder;

import com.github.javafaker.Faker;
import com.sparky.trak.game.service.ConsoleService;
import com.sparky.trak.game.service.dto.ConsoleDto;
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
public class ConsoleSeeder implements Runnable {

    @Setter
    @Value("${seeding.console.count ?: 10}")
    private int consoleCount;

    private final ConsoleService consoleService;

    @Override
    public void run() {
        Faker faker = new Faker();

        IntStream.range(0, consoleCount).forEach(i -> {
            ConsoleDto consoleDto = new ConsoleDto();
            consoleDto.setName(faker.book().title());
            consoleDto.setDescription(faker.lorem().characters(2000));
            consoleDto.setReleaseDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

            consoleService.save(consoleDto);
        });
    }
}
