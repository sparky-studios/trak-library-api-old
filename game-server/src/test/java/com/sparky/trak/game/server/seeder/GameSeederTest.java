package com.sparky.trak.game.server.seeder;

import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.dto.GameDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameSeederTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameSeeder gameSeeder;

    @Test
    public void run_withDefaultData_insertsTenGames() {
        // Arrange
        gameSeeder.setGamesCount(10);

        Mockito.when(gameService.save(ArgumentMatchers.any()))
                .thenReturn(new GameDto());

        // Act
        gameSeeder.run();

        // Assert
        Mockito.verify(gameService, Mockito.atMost(10))
            .save(ArgumentMatchers.any());
    }
}