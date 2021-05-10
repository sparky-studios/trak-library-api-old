package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.DownloadableContentImage;
import com.sparkystudios.traklibrary.game.domain.ImageSize;
import com.sparkystudios.traklibrary.game.repository.DownloadableContentImageRepository;
import com.sparkystudios.traklibrary.game.service.DownloadableContentImageService;
import com.sparkystudios.traklibrary.game.service.client.ImageClient;
import com.sparkystudios.traklibrary.game.service.dto.ImageDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DownloadableContentImageServiceImpl implements DownloadableContentImageService {

    private static final String ENTITY_EXISTS_MESSAGE = "downloadable-content-image.exception.image-exists";
    private static final String NOT_FOUND_MESSAGE = "downloadable-content-image.exception.not-found";

    private final DownloadableContentImageRepository downloadableContentImageRepository;
    private final MessageSource messageSource;
    private final ImageClient imageClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(long downloadableContentId, ImageSize imageSize, MultipartFile multipartFile) {
        // Can't upload additional images for the same game.
        if (downloadableContentImageRepository.existsByDownloadableContentIdAndImageSize(downloadableContentId, imageSize)) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] {imageSize, downloadableContentId}, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create a reference to the new game image.
        var downloadableContentImage = new DownloadableContentImage();
        downloadableContentImage.setDownloadableContentId(downloadableContentId);
        downloadableContentImage.setFilename(multipartFile.getOriginalFilename());
        downloadableContentImage.setImageSize(imageSize);

        // Save the new game image to the database.
        downloadableContentImageRepository.save(downloadableContentImage);

        // Call off to the image micro-service and upload the image data to the chosen image provider.
        imageClient.uploadDownloadableContentImage(multipartFile, downloadableContentId);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDataDto download(long downloadableContentId, ImageSize imageSize) {
        Optional<DownloadableContentImage> downloadableContentImage = downloadableContentImageRepository
                .findByDownloadableContentIdAndImageSize(downloadableContentId, imageSize);

        if (downloadableContentImage.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] {downloadableContentId}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        var imageDataDto = new ImageDataDto();
        imageDataDto.setFilename(downloadableContentImage.get().getFilename());
        // Download the image data from the image service, if available.
        imageDataDto.setContent(imageClient.downloadDownloadableContentImage(downloadableContentImage.get().getFilename()));

        return imageDataDto;
    }
}
