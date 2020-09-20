package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.repository.GameImageRepository;
import com.sparkystudios.traklibrary.game.service.client.ImageClient;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GameImageServiceImplTest {

    @Mock
    private GameImageRepository gameImageRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ImageClient imageClient;

    @InjectMocks
    private GameImageServiceImpl gameImageService;

    @Test
    void upload_withExistingGameImage_throwsEntityExistsException() {
        // Arrange
        Mockito.when(gameImageRepository.existsByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // Assert
        Assertions.assertThrows(EntityExistsException.class, () -> gameImageService.upload(0L, multipartFile));
    }

    @Test
    void upload_withNewGameImage_invokesSaveAndImageClientUpload() {
        // Arrange
        Mockito.when(gameImageRepository.existsByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename())
                .thenReturn("filename");

        Mockito.when(gameImageRepository.save(ArgumentMatchers.any()))
                .thenReturn(new GameImage());

        Mockito.doNothing().when(imageClient)
                .uploadGameImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());

        // Act
        gameImageService.upload(0L, multipartFile);

        // Assert
        Mockito.verify(gameImageRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(imageClient, Mockito.atMostOnce())
                .uploadGameImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    void download_withNonExistentGameImage_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(gameImageRepository.findByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> gameImageService.download(0L));
    }

    @Test
    void download_withValidGameImage_invokesImageClientDownload() {
        // Arrange
        byte[] imageData = new byte[] { 'a', 'b' };

        GameImage gameImage = new GameImage();
        gameImage.setFilename("filename.txt");

        Mockito.when(gameImageRepository.findByGameId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(gameImage));

        Mockito.when(imageClient.downloadGameImage(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
                .thenReturn(imageData);

        // Act
        ImageDataDto imageDataDto = gameImageService.download(0L);

        // Assert
        Mockito.verify(imageClient, Mockito.atMostOnce())
                .downloadGameImage(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        Assertions.assertEquals(gameImage.getFilename(), imageDataDto.getFilename(), "The filenames should match.");
        Assertions.assertEquals(imageData, imageDataDto.getContent(), "The contents of the file should match what the image client returns.");
    }
}
