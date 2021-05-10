package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.CompanyImage;
import com.sparkystudios.traklibrary.game.repository.CompanyImageRepository;
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
class CompanyImageServiceImplTest {

    @Mock
    private CompanyImageRepository companyImageRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ImageClient imageClient;

    @InjectMocks
    private CompanyImageServiceImpl companyImageService;

    @Test
    void upload_withExistingCompanyImage_throwsEntityExistsException() {
        // Arrange
        Mockito.when(companyImageRepository.existsByCompanyId(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);

        // Assert
        Assertions.assertThrows(EntityExistsException.class,
                () -> companyImageService.upload(0L, multipartFile));
    }

    @Test
    void upload_withNewCompanyImage_invokesSaveAndImageClientUpload() {
        // Arrange
        Mockito.when(companyImageRepository.existsByCompanyId(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(multipartFile.getOriginalFilename())
                .thenReturn("filename");

        Mockito.when(companyImageRepository.save(ArgumentMatchers.any()))
                .thenReturn(new CompanyImage());

        Mockito.doNothing().when(imageClient)
                .uploadCompanyImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());

        // Act
        companyImageService.upload(0L, multipartFile);

        // Assert
        Mockito.verify(companyImageRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());

        Mockito.verify(imageClient, Mockito.atMostOnce())
                .uploadCompanyImage(ArgumentMatchers.any(), ArgumentMatchers.anyLong());
    }

    @Test
    void download_withNonExistentCompanyImage_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(companyImageRepository.findByCompanyId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> companyImageService.download(0L));
    }

    @Test
    void download_withValidCompanyImage_invokesImageClientDownload() {
        // Arrange
        byte[] imageData = new byte[] { 'a', 'b' };

        CompanyImage companyImage = new CompanyImage();
        companyImage.setFilename("filename.txt");

        Mockito.when(companyImageRepository.findByCompanyId(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(companyImage));

        Mockito.when(imageClient.downloadCompanyImage(ArgumentMatchers.anyString()))
                .thenReturn(imageData);

        // Act
        ImageDataDto imageDataDto = companyImageService.download(0L);

        // Assert
        Mockito.verify(imageClient, Mockito.atMostOnce())
                .downloadCompanyImage(ArgumentMatchers.anyString());

        Assertions.assertEquals(companyImage.getFilename(), imageDataDto.getFilename(), "The filenames should match.");
        Assertions.assertEquals(imageData, imageDataDto.getContent(), "The contents of the file should match what the image client returns.");
    }
}
