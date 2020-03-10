package com.sparky.maidcafe.game.service.mapper;

import com.sparky.maidcafe.game.domain.Console;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ConsoleMapperTest {

    @Test
    public void consoleToConsoleDto_withConsole_mapsFields() {
        // Arrange
        Console console = new Console();
        console.setId(5L);
        console.setName("test-name");
        console.setDescription("test-description");
        console.setReleaseDate(LocalDate.now());
        console.setVersion(1L);

        // Act
        ConsoleDto result = ConsoleMapper.INSTANCE.consoleToConsoleDto(console);

        // Assert
        Assertions.assertEquals(console.getId(), result.getId(), "The mapped ID does not match the entity.");
        Assertions.assertEquals(console.getName(), result.getName(), "The mapped title does not match the entity.");
        Assertions.assertEquals(console.getDescription(), result.getDescription(), "The mapped description does not match the entity.");
        Assertions.assertEquals(console.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the entity.");
        Assertions.assertEquals(console.getVersion(), result.getVersion(), "The mapped version does not match the entity.");
    }

    @Test
    public void consoleDtoToConsole_withConsoleDto_mapsFields() {
        // Arrange
        ConsoleDto consoleDto = new ConsoleDto();
        consoleDto.setId(5L);
        consoleDto.setName("test-name");
        consoleDto.setDescription("test-description");
        consoleDto.setReleaseDate(LocalDate.now());
        consoleDto.setVersion(1L);

        // Act
        Console result = ConsoleMapper.INSTANCE.consoleDtoToConsole(consoleDto);

        // Assert
        Assertions.assertEquals(consoleDto.getId(), result.getId(), "The mapped ID does not match the DTO.");
        Assertions.assertEquals(consoleDto.getName(), result.getName(), "The mapped title does not match the DTO.");
        Assertions.assertEquals(consoleDto.getDescription(), result.getDescription(), "The mapped description does not match the DTO.");
        Assertions.assertEquals(consoleDto.getReleaseDate(), result.getReleaseDate(), "The mapped release date does not match the DTO.");
        Assertions.assertEquals(consoleDto.getVersion(), result.getVersion(), "The mapped version does not match the DTO.");
    }
}
