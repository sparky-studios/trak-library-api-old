package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.PlatformImage;
import com.sparkystudios.traklibrary.game.repository.PlatformImageRepository;
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
class PlatformImageServiceImplTest {

    @Mock
    private PlatformImageRepository platformImageRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ImageClient imageClient;

    @InjectMocks
    private PlatformImageServiceImpl platformImageService;

    @Test
    void upload_withExistingPlatformImage_throwsEntityExistsException() {
        // Arrange
        Mockito.when(platformImageRepository.existsByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // Assert
        Assertions.assertThrows(EntityExistsException.class,
                () -> platformImageService.upload(0L, multipartFile));
    }

    @Test
    void upload_withNewPlatformImage_invokesSaveAndImageClientUpload() {
        // Arrange
        Mockito.when(platformImageRepository.existsByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename())
                .thenReturn("filename");

        Mockito.when(platformImageRepository.save(ArgumentMatchers.any()))
                .thenReturn(new PlatformImage());

        Mockito.doNothing().when(imageClient)
                .uploadPlatformImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());

        // Act
        platformImageService.upload(0L, multipartFile);

        // Assert
        Mockito.verify(platformImageRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(imageClient, Mockito.atMostOnce())
                .uploadPlatformImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    void download_withNonExistentPlatformImage_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(platformImageRepository.findByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> platformImageService.download(0L));
    }

    @Test
    void download_withValidPlatformImage_invokesImageClientDownload() {
        // Arrange
        byte[] imageData = new byte[] { 'a', 'b' };

        PlatformImage platformImage = new PlatformImage();
        platformImage.setFilename("filename.txt");

        Mockito.when(platformImageRepository.findByPlatformId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(platformImage));

        Mockito.when(imageClient.downloadCompanyImage(ArgumentMatchers.anyString()))
                .thenReturn(imageData);

        // Act
        ImageDataDto imageDataDto = platformImageService.download(0L);

        // Assert
        Mockito.verify(imageClient, Mockito.atMostOnce())
                .downloadPlatformImage(ArgumentMatchers.anyString());

        Assertions.assertEquals(platformImage.getFilename(), imageDataDto.getFilename(), "The filenames should match.");
        Assertions.assertEquals(imageData, imageDataDto.getContent(), "The contents of the file should match what the image client returns.");
    }
}
