package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.CompanyImage;
import com.sparkystudios.traklibrary.game.repository.CompanyImageRepository;
import com.sparkystudios.traklibrary.game.service.CompanyImageService;
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
public class CompanyImageServiceImpl implements CompanyImageService {

    private static final String ENTITY_EXISTS_MESSAGE = "company-image.exception.image-exists";
    private static final String NOT_FOUND_MESSAGE = "company-image.exception.not-found";

    private final CompanyImageRepository companyImageRepository;
    private final MessageSource messageSource;
    private final ImageClient imageClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(long companyId, MultipartFile multipartFile) {
        // Can't upload additional images for the same game.
        if (companyImageRepository.existsByCompanyId(companyId)) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { companyId }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create a reference to the new game image.
        var companyImage = new CompanyImage();
        companyImage.setCompanyId(companyId);
        companyImage.setFilename(multipartFile.getOriginalFilename());

        // Save the new game image to the database.
        companyImageRepository.save(companyImage);

        // Call off to the image micro-service and upload the image data to the chosen image provider.
        imageClient.uploadCompanyImage(multipartFile, companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDataDto download(long companyId) {
        Optional<CompanyImage> companyImage = companyImageRepository.findByCompanyId(companyId);

        if (companyImage.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] {companyId}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        var imageDataDto = new ImageDataDto();
        imageDataDto.setFilename(companyImage.get().getFilename());
        // Download the image data from the image service, if available.
        imageDataDto.setContent(imageClient.downloadCompanyImage(companyImage.get().getFilename()));

        return imageDataDto;
    }
}
