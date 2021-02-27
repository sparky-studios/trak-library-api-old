package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.DownloadableContent;
import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryDownloadableContent;
import com.sparkystudios.traklibrary.game.domain.GameUserEntryPlatform;
import com.sparkystudios.traklibrary.game.domain.Platform;
import com.sparkystudios.traklibrary.game.repository.DownloadableContentRepository;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GameUserEntryRepository;
import com.sparkystudios.traklibrary.game.repository.PlatformRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.service.GameUserEntryService;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.dto.request.GameUserEntryRequest;
import com.sparkystudios.traklibrary.game.service.mapper.GameUserEntryMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.exception.InvalidUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class GameUserEntryServiceImpl implements GameUserEntryService {

    private static final String INVALID_USER_MESSAGE = "game-user-entry.exception.invalid-user";
    private static final String ENTITY_EXISTS_MESSAGE = "game-user-entry.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "game-user-entry.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final GameUserEntryRepository gameUserEntryRepository;
    private final PlatformRepository platformRepository;
    private final DownloadableContentRepository downloadableContentRepository;
    private final GameRepository gameRepository;
    private final GameUserEntryMapper gameUserEntryMapper;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameUserEntryDto save(GameUserEntryRequest gameUserEntryRequest) {
        Objects.requireNonNull(gameUserEntryRequest);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryRequest.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (gameUserEntryRepository.existsById(gameUserEntryRequest.getGameUserEntryId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { gameUserEntryRequest.getGameUserEntryId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        // Create the game user entry entity from the request and persist it.
        GameUserEntry gameUserEntry = new GameUserEntry();
        gameUserEntry.setUserId(gameUserEntryRequest.getUserId());
        gameUserEntry.setGameId(gameUserEntryRequest.getGameId());
        gameUserEntry.setRating(gameUserEntryRequest.getRating());
        gameUserEntry.setStatus(gameUserEntryRequest.getStatus());

        // Loop through each requested platform and add it to the user entry.
        gameUserEntryRequest.getPlatformIds().forEach(platformId -> {
            Optional<Platform> platform = platformRepository.findById(platformId);
            // Create a platform reference and add it to the game user entry entity if the platform exists.
            if (platform.isPresent()) {
                GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
                gameUserEntryPlatform.setPlatform(platform.get());

                gameUserEntry.addGameUserEntryPlatform(gameUserEntryPlatform);
            }
        });

        // Loop through eahc request dlc and add it to the user entry.
        gameUserEntryRequest.getDownloadableContentIds().forEach(downloadableContentId -> {
            Optional<DownloadableContent> downloadableContent = downloadableContentRepository.findById(downloadableContentId);
            // Create a platform reference and add it to the game user entry entity if the platform exists.
            if (downloadableContent.isPresent()) {
                GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
                gameUserEntryDownloadableContent.setDownloadableContent(downloadableContent.get());

                gameUserEntry.addGameUserEntryDownloadableContent(gameUserEntryDownloadableContent);
            }
        });

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository.save(gameUserEntry));
    }

    @Override
    @Transactional(readOnly = true)
    public GameUserEntryDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameUserEntryDto> findGameUserEntriesByGameId(long gameId, GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        Specification<GameUserEntry> specification = (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("gameId"), gameId);

        return gameUserEntryRepository
                .findAll(specification.and(gameUserEntrySpecification), pageable)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGameUserEntriesByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameUserEntryRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameUserEntryDto> findAll(GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable) {
        return StreamSupport.stream(gameUserEntryRepository.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameUserEntrySpecification gameUserEntrySpecification) {
        Objects.requireNonNull(gameUserEntrySpecification);

        return gameUserEntryRepository.count(gameUserEntrySpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameUserEntryDto update(GameUserEntryRequest gameUserEntryRequest) {
        Objects.requireNonNull(gameUserEntryRequest);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryRequest.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        Optional<GameUserEntry> gameUserEntry = gameUserEntryRepository.findById(gameUserEntryRequest.getGameUserEntryId());
        if (gameUserEntry.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { gameUserEntryRequest.getGameUserEntryId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        // Update the basic information of the existing game user entry entity.
        GameUserEntry gue = gameUserEntry.get();
        gue.setUserId(gameUserEntryRequest.getUserId());
        gue.setGameId(gameUserEntryRequest.getGameId());
        gue.setRating(gameUserEntryRequest.getRating());
        gue.setStatus(gameUserEntryRequest.getStatus());

        // Remove any game user entry platforms that have been removed from the request.
        gue.getGameUserEntryPlatforms()
                .removeIf(gameUserEntryPlatform -> !gameUserEntryRequest.getPlatformIds().contains(gameUserEntryPlatform.getPlatformId()));

        // Loop through each and add any new entries.
        gameUserEntryRequest.getPlatformIds().forEach(platformId -> {

            boolean contains = gue.getGameUserEntryPlatforms()
                    .stream()
                    .anyMatch(gameUserEntryPlatform -> gameUserEntryPlatform.getPlatformId() == platformId);

            if (!contains) {
                Optional<Platform> platform = platformRepository.findById(platformId);
                // Create a platform reference and add it to the game user entry entity if the platform exists.
                if (platform.isPresent()) {
                    GameUserEntryPlatform gameUserEntryPlatform = new GameUserEntryPlatform();
                    gameUserEntryPlatform.setPlatform(platform.get());

                    gue.addGameUserEntryPlatform(gameUserEntryPlatform);
                }
            }
        });

        // Remove any game user entry downloadable contents that have been removed from the request.
        gue.getGameUserEntryDownloadableContents()
                .removeIf(gameUserEntryDownloadableContent -> !gameUserEntryRequest.getDownloadableContentIds().contains(gameUserEntryDownloadableContent.getDownloadableContentId()));

        // Loop through each and add any new entries.
        gameUserEntryRequest.getDownloadableContentIds().forEach(gameUserEntryDownloadableContentId -> {

            boolean contains = gue.getGameUserEntryDownloadableContents()
                    .stream()
                    .anyMatch(gameUserEntryDownloadableContent -> gameUserEntryDownloadableContent.getDownloadableContentId() == gameUserEntryDownloadableContentId);

            if (!contains) {
                Optional<DownloadableContent> downloadableContent = downloadableContentRepository.findById(gameUserEntryDownloadableContentId);
                // Create a downloadable content reference and add it to the game user entry entity if the platform exists.
                if (downloadableContent.isPresent()) {
                    GameUserEntryDownloadableContent gameUserEntryDownloadableContent = new GameUserEntryDownloadableContent();
                    gameUserEntryDownloadableContent.setDownloadableContent(downloadableContent.get());

                    gue.addGameUserEntryDownloadableContent(gameUserEntryDownloadableContent);
                }
            }
        });

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository.save(gue));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        Optional<GameUserEntry> gameUserEntry = gameUserEntryRepository.findById(id);

        if (gameUserEntry.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntry.get().getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        gameUserEntryRepository.deleteById(id);
    }
}
