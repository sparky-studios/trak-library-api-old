package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.*;
import com.traklibrary.game.repository.specification.GameSpecification;
import com.traklibrary.game.service.GameService;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.dto.GameDto;
import com.traklibrary.game.service.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private static final String ENTITY_EXISTS_MESSAGE = "game.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "game.exception.not-found";
    private static final String GENRE_NOT_FOUND_MESSAGE = "genre.exception.not-found";
    private static final String PLATFORM_NOT_FOUND_MESSAGE = "platform.exception.not-found";
    private static final String DEVELOPER_NOT_FOUND_MESSAGE = "developer.exception.not-found";
    private static final String PUBLISHER_NOT_FOUND_MESSAGE = "publisher.exception.not-found";

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreXrefRepository gameGenreXrefRepository;
    private final PlatformRepository platformRepository;
    private final GamePlatformXrefRepository gamePlatformXrefRepository;
    private final DeveloperRepository developerRepository;
    private final GameDeveloperXrefRepository gameDeveloperXrefRepository;
    private final PublisherRepository publisherRepository;
    private final GamePublisherXrefRepository gamePublisherXrefRepository;
    private final GameMapper gameMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public GameDto save(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(gameDto)));
    }

    @Override
    public GameDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return gameMapper.gameToGameDto(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GameDto> findGamesByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("genreId"), genreId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
    }

    @Override
    public long countGamesByGenreId(long genreId) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("genreId"), genreId));
    }

    @Override
    public Iterable<GameDto> findGamesByPlatformId(long platformId, Pageable pageable) {
        if (!platformRepository.existsById(platformId)) {
            String errorMessage = messageSource
                    .getMessage(PLATFORM_NOT_FOUND_MESSAGE, new Object[] { platformId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePlatformXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("platformId"), platformId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
    }

    @Override
    public long countGamesByPlatformId(long platformId) {
        if (!platformRepository.existsById(platformId)) {
            String errorMessage = messageSource
                    .getMessage(PLATFORM_NOT_FOUND_MESSAGE, new Object[] { platformId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePlatformXrefRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("platformId"), platformId));
    }

    @Override
    public Iterable<GameDto> findGamesByDeveloperId(long developerId, Pageable pageable) {
        if (!developerRepository.existsById(developerId)) {
            String errorMessage = messageSource
                    .getMessage(DEVELOPER_NOT_FOUND_MESSAGE, new Object[] { developerId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameDeveloperXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("developerId"), developerId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
    }

    @Override
    public long countGamesByDeveloperId(long developerId) {
        if (!developerRepository.existsById(developerId)) {
            String errorMessage = messageSource
                    .getMessage(DEVELOPER_NOT_FOUND_MESSAGE, new Object[] { developerId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameDeveloperXrefRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("developerId"), developerId));
    }

    @Override
    public Iterable<GameDto> findGamesByPublisherId(long publisherId, Pageable pageable) {
        if (!publisherRepository.existsById(publisherId)) {
            String errorMessage = messageSource
                    .getMessage(PUBLISHER_NOT_FOUND_MESSAGE, new Object[] { publisherId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePublisherXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("publisherId"), publisherId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
    }

    @Override
    public long countGamesByPublisherId(long publisherId) {
        if (!publisherRepository.existsById(publisherId)) {
            String errorMessage = messageSource
                    .getMessage(PUBLISHER_NOT_FOUND_MESSAGE, new Object[] { publisherId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePublisherXrefRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("publisherId"), publisherId));
    }

    @Override
    public Iterable<GameDto> findAll() {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .map(gameMapper::gameToGameDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<GameDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameMapper::gameToGameDto);
    }

    @Override
    public long count(GameSpecification gameSpecification) {
        Objects.requireNonNull(gameSpecification);

        return gameRepository.count(gameSpecification);
    }

    @Override
    public GameDto update(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (!gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(gameDto)));
    }

    @Override
    public GameDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameDto patched = patchService.patch(jsonMergePatch, findById(id), GameDto.class);
        // Save to the repository and convert it back to a GameDto.
        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!gameRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        gameRepository.deleteById(id);
    }
}
