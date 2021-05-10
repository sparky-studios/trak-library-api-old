package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.PlatformImage;
import com.sparkystudios.traklibrary.game.repository.PlatformImageRepository;
import com.sparkystudios.traklibrary.game.service.PlatformImageService;
import com.sparkystudios.traklibrary.game.service.client.ImageClient;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PlatformImageServiceImpl implements PlatformImageService {

    private static final String ENTITY_EXISTS_MESSAGE = "platform-image.exception.image-exists";
    private static final String NOT_FOUND_MESSAGE = "platform-image.exception.not-found";

    private final PlatformImageRepository platformImageRepository;
    private final MessageSource messageSource;
    private final ImageClient imageClient;

    @Override
    public void upload(long platformId, MultipartFile multipartFile) {
        // Can't upload additional images for the same game.
        if (platformImageRepository.existsByPlatformId(platformId)) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { platformId }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create a reference to the new game image.
        var companyImage = new PlatformImage();
        companyImage.setPlatformId(platformId);
        companyImage.setFilename(multipartFile.getOriginalFilename());

        // Save the new game image to the database.
        platformImageRepository.save(companyImage);

        // Call off to the image micro-service and upload the image data to the chosen image provider.
        imageClient.uploadPlatformImage(multipartFile, platformId);
    }

    @Override
    public ImageDataDto download(long platformId) {
        Optional<PlatformImage> platformImage = platformImageRepository.findByPlatformId(platformId);

        if (platformImage.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] {platformId}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        var imageDataDto = new ImageDataDto();
        imageDataDto.setFilename(platformImage.get().getFilename());
        // Download the image data from the image service, if available.
        imageDataDto.setContent(imageClient.downloadCompanyImage(platformImage.get().getFilename()));

        return imageDataDto;
    }
}
