package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.GameImage;
import com.sparky.trak.game.repository.GameImageRepository;
import com.sparky.trak.game.service.GameImageService;
import com.sparky.trak.game.service.client.ImageClient;
import com.sparky.trak.game.service.dto.ImageDataDto;
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

    private final GameImageRepository gameImageRepository;
    private final MessageSource messageSource;
    private final ImageClient imageClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(long gameId, MultipartFile multipartFile) {
        // Can't upload additional images for the same game.
        if (gameImageRepository.existsByGameId(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game-image.exception.image-exists", new Object[] {gameId}, LocaleContextHolder.getLocale());

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
    public ImageDataDto download(long gameId) {
        Optional<GameImage> gameImage = gameImageRepository.findByGameId(gameId);

        if (!gameImage.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("game-image.exception.not-found", new Object[] {gameId}, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        ImageDataDto imageDataDto = new ImageDataDto();
        imageDataDto.setFilename(gameImage.get().getFilename());
        // Download the image data from the image service, if available.
        imageDataDto.setContent(imageClient.downloadGameImage(gameImage.get().getFilename(), gameId));

        return imageDataDto;
    }
}
