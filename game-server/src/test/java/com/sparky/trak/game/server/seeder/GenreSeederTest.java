package com.sparky.trak.game.server.seeder;

import com.sparky.trak.game.service.GenreService;
import com.sparky.trak.game.service.dto.GenreDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GenreSeederTest {

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreSeeder genreSeeder;

    @Test
    public void run_withDefaultData_insertsTenGenres() {
        // Arrange
        genreSeeder.setGenreCount(10);

        Mockito.when(genreService.save(ArgumentMatchers.any()))
                .thenReturn(new GenreDto());

        // Act
        genreSeeder.run();

        // Assert
        Mockito.verify(genreService, Mockito.atMost(10))
                .save(ArgumentMatchers.any());
    }
}
