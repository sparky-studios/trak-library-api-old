package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.DownloadableContentImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.repository.DownloadableContentImageRepository;
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
class DownloadableContentImageServiceImplTest {

    @Mock
    private DownloadableContentImageRepository downloadableContentImageRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ImageClient imageClient;

    @InjectMocks
    private DownloadableContentImageServiceImpl downloadableContentImageService;

    @Test
    void upload_withExistingDownloadableContentImage_throwsEntityExistsException() {
        // Arrange
        Mockito.when(downloadableContentImageRepository.existsByDownloadableContentIdAndImageSize(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // Assert
        Assertions.assertThrows(EntityExistsException.class,
                () -> downloadableContentImageService.upload(0L, ImageSize.SMALL, multipartFile));
    }

    @Test
    void upload_withNewDownloadableContentImage_invokesSaveAndImageClientUpload() {
        // Arrange
        Mockito.when(downloadableContentImageRepository.existsByDownloadableContentIdAndImageSize(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(false);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename())
                .thenReturn("filename");

        Mockito.when(downloadableContentImageRepository.save(ArgumentMatchers.any()))
                .thenReturn(new DownloadableContentImage());

        Mockito.doNothing().when(imageClient)
                .uploadDownloadableContentImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());

        // Act
        downloadableContentImageService.upload(0L, ImageSize.MEDIUM, multipartFile);

        // Assert
        Mockito.verify(downloadableContentImageRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(imageClient, Mockito.atMostOnce())
                .uploadDownloadableContentImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    void download_withNonExistentDownloadableContentImage_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(downloadableContentImageRepository.findByDownloadableContentIdAndImageSize(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> downloadableContentImageService.download(0L, ImageSize.MEDIUM));
    }

    @Test
    void download_withValidDownloadableContentImage_invokesImageClientDownload() {
        // Arrange
        byte[] imageData = new byte[] { 'a', 'b' };

        DownloadableContentImage downloadableContentImage = new DownloadableContentImage();
        downloadableContentImage.setFilename("filename.txt");

        Mockito.when(downloadableContentImageRepository.findByDownloadableContentIdAndImageSize(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
                .thenReturn(Optional.of(downloadableContentImage));

        Mockito.when(imageClient.downloadDownloadableContentImage(ArgumentMatchers.anyString()))
                .thenReturn(imageData);

        // Act
        ImageDataDto imageDataDto = downloadableContentImageService.download(0L, ImageSize.MEDIUM);

        // Assert
        Mockito.verify(imageClient, Mockito.atMostOnce())
                .downloadDownloadableContentImage(ArgumentMatchers.anyString());

        Assertions.assertEquals(downloadableContentImage.getFilename(), imageDataDto.getFilename(), "The filenames should match.");
        Assertions.assertEquals(imageData, imageDataDto.getContent(), "The contents of the file should match what the image client returns.");
    }
}
