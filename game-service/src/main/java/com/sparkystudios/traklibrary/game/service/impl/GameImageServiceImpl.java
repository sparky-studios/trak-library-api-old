package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.GameImage;
import com.sparkystudios.traklibrary.game.repository.GameImageRepository;
import com.sparkystudios.traklibrary.game.service.GameImageService;
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
public class GameImageServiceImpl implements GameImageService {

    private static final String ENTITY_EXISTS_MESSAGE = "game-image.exception.image-exists";
    private static final String NOT_FOUND_MESSAGE = "game-image.exception.not-found";

    private final GameImageRepository gameImageRepository;
    private final MessageSource messageSource;
    private final ImageClient imageClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(long gameId, MultipartFile multipartFile) {
        // Can't upload additional images for the same game.
        if (gameImageRepository.existsByGameId(gameId)) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] {gameId}, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create a reference to the new game image.
        GameImage gameImage = new GameImage();
        gameImage.setGameId(gameId);
        gameImage.setFilename(multipartFile.getOriginalFilename());

        // Save the new game image to the database.
        gameImageRepository.save(gameImage);

        // Call off to the image micro-service and upload the image data to the chosen image provider.
        imageClient.uploadGameImage(multipartFile, gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDataDto download(long gameId) {
        Optional<GameImage> gameImage = gameImageRepository.findByGameId(gameId);

        if (!gameImage.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] {gameId}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        ImageDataDto imageDataDto = new ImageDataDto();
        imageDataDto.setFilename(gameImage.get().getFilename());
        // Download the image data from the image service, if available.
        imageDataDto.setContent(imageClient.downloadGameImage(gameImage.get().getFilename(), gameId));

        return imageDataDto;
    }
}
