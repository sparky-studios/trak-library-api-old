package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.repository.DownloadableContentRepository;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.mapper.DownloadableContentMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExtendWith(MockitoExtension.class)
class DownloadableContentServiceTest {

    @Mock
    private DownloadableContentRepository downloadableContentRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private MessageSource messageSource;

    @Spy
    private DownloadableContentMapper downloadableContentMapper;

    @InjectMocks
    private DownloadableContentServiceImpl downloadableContentService;

    @Test
    void findById_withEmptyOptional_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(downloadableContentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> downloadableContentService.findById(0L));
    }

    @Test
    void findById_withValidDownloadableContent_returnsDownloadableContentDto() {
        // Arrange
        DownloadableContent downloadableContent = new DownloadableContent();
        downloadableContent.setId(1L);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(downloadableContentRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(downloadableContent));

        Mockito.when(downloadableContentMapper.fromDownloadableContent(ArgumentMatchers.any()))
                .thenReturn(new DownloadableContentDto());

        // Act
        DownloadableContentDto result = downloadableContentService.findById(0L);

        // Assert
        Assertions.assertNotNull(result, "The mapped result should not be null.");

        Mockito.verify(downloadableContentMapper, Mockito.atMostOnce())
                .fromDownloadableContent(ArgumentMatchers.any());
    }

    @Test
    void findDownloadableContentsByGameId_withNonExistentGame_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> downloadableContentService.findDownloadableContentsByGameId(0L));
    }

    @Test
    void findDownloadableContentsByGameId_withNoDownloadableContents_returnsEmptyList() {
        // Arrange
        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(new Game()));

        // Act
        List<DownloadableContentDto> result = StreamSupport.stream(downloadableContentService.findDownloadableContentsByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertTrue(result.isEmpty(), "The result should be empty if no games are returned.");

        Mockito.verify(downloadableContentMapper, Mockito.never())
                .fromDownloadableContent(ArgumentMatchers.any());
    }

    @Test
    void findDownloadableContentsByGameId_withMultipleDownloadableContents_returnsList() {
        // Arrange
        DownloadableContent downloadableContent1 = new DownloadableContent();
        downloadableContent1.setName("dlc-1");

        DownloadableContent downloadableContent2 = new DownloadableContent();
        downloadableContent2.setName("dlc-2");

        Game game = new Game();
        game.addDownloadableContent(downloadableContent1);
        game.addDownloadableContent(downloadableContent2);

        Mockito.when(gameRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(game));

        // Act
        List<DownloadableContentDto> result = StreamSupport.stream(downloadableContentService.findDownloadableContentsByGameId(0L).spliterator(), false)
                .collect(Collectors.toList());

        // Assert
        Assertions.assertFalse(result.isEmpty(), "The result should not be empty if dlc are returned.");
        Assertions.assertEquals(2, result.size(), "There should be only two dlc if there are two dlc associated with the game.");

        Mockito.verify(downloadableContentMapper, Mockito.atMost(2))
                .fromDownloadableContent(ArgumentMatchers.any());
    }
}
