package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.*;
import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.service.GameService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Collection;
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
    private static final String FRANCHISE_NOT_FOUND_MESSAGE = "franchise.exception.not-found";
    private static final String PUBLISHER_NOT_FOUND_MESSAGE = "publisher.exception.not-found";

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final DeveloperRepository developerRepository;
    private final PublisherRepository publisherRepository;
    private final FranchiseRepository franchiseRepository;
    private final GameMapper gameMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto save(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        var game = gameMapper.toGame(gameDto);
        game.getAgeRatings().forEach(ageRating -> ageRating.setGame(game));
        game.getReleaseDates().forEach(gameReleaseDate -> gameReleaseDate.setGame(game));
        game.getDownloadableContents().forEach(downloadableContent -> downloadableContent.setGame(game));

        // We need to retrieve the game by the new ID as we want the release dates joined to the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(readOnly = true)
    public GameDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        return gameMapper.fromGame(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public GameDto findBySlug(String slug) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "slug", slug }, LocaleContextHolder.getLocale());

        return gameMapper.fromGame(gameRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findGamesByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByGenresId(genreId, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByGenreId(long genreId) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByGenresId(genreId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto saveGenresForGameId(long id, @NonNull Collection<Long> genreIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Clear all existing genres before updating them with the new ID's provided.
        game.getGenres().clear();

        // Get all of the matching genres for the ID's provided.
        Iterable<Genre> genres = genreRepository.findAllById(genreIds);

        // Add all the genres within the collection.
        genres.forEach(game::addGenre);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto updateGenresForGameId(long id, @NonNull Collection<Long> genreIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Get all of the matching genres for the ID's provided.
        Iterable<Genre> genres = genreRepository.findAllById(genreIds);

        // Add all the genres within the collection.
        genres.forEach(game::addGenre);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findGamesByPlatformId(long platformId, Pageable pageable) {
        if (!platformRepository.existsById(platformId)) {
            String errorMessage = messageSource
                    .getMessage(PLATFORM_NOT_FOUND_MESSAGE, new Object[] { platformId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByPlatformsId(platformId, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByPlatformId(long platformId) {
        if (!platformRepository.existsById(platformId)) {
            String errorMessage = messageSource
                    .getMessage(PLATFORM_NOT_FOUND_MESSAGE, new Object[] { platformId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByPlatformsId(platformId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto savePlatformsForGameId(long id, @NonNull Collection<Long> platformsIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Clear all existing platforms before updating them with the new ID's provided.
        game.getPlatforms().clear();

        // Get all of the matching genres for the ID's provided.
        Iterable<Platform> platforms = platformRepository.findAllById(platformsIds);

        // Add all the platforms within the collection.
        platforms.forEach(game::addPlatform);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto updatePlatformsForGameId(long id, @NonNull Collection<Long> platformIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Get all of the matching platforms for the ID's provided.
        Iterable<Platform> platforms = platformRepository.findAllById(platformIds);

        // Add all the platforms within the collection.
        platforms.forEach(game::addPlatform);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findGamesByDeveloperId(long developerId, Pageable pageable) {
        if (!developerRepository.existsById(developerId)) {
            String errorMessage = messageSource
                    .getMessage(DEVELOPER_NOT_FOUND_MESSAGE, new Object[] { developerId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByDevelopersId(developerId, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByDeveloperId(long developerId) {
        if (!developerRepository.existsById(developerId)) {
            String errorMessage = messageSource
                    .getMessage(DEVELOPER_NOT_FOUND_MESSAGE, new Object[] { developerId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByDevelopersId(developerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto saveDevelopersForGameId(long id, @NonNull Collection<Long> developerId) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Clear all existing developers before updating them with the new ID's provided.
        game.getDevelopers().clear();

        // Get all of the matching developers for the ID's provided.
        Iterable<Developer> developers = developerRepository.findAllById(developerId);

        // Add all the developers within the collection.
        developers.forEach(game::addDeveloper);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto updateDevelopersForGameId(long id, @NonNull Collection<Long> developerId) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Get all of the matching developers for the ID's provided.
        Iterable<Developer> developers = developerRepository.findAllById(developerId);

        // Add all the developers within the collection.
        developers.forEach(game::addDeveloper);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findGamesByPublisherId(long publisherId, Pageable pageable) {
        if (!publisherRepository.existsById(publisherId)) {
            String errorMessage = messageSource
                    .getMessage(PUBLISHER_NOT_FOUND_MESSAGE, new Object[] { publisherId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByPublishersId(publisherId, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByPublisherId(long publisherId) {
        if (!publisherRepository.existsById(publisherId)) {
            String errorMessage = messageSource
                    .getMessage(PUBLISHER_NOT_FOUND_MESSAGE, new Object[] { publisherId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByPublishersId(publisherId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto savePublishersForGameId(long id, @NonNull Collection<Long> publisherIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Clear all existing developers before updating them with the new ID's provided.
        game.getDevelopers().clear();

        // Get all of the matching publishers for the ID's provided.
        Iterable<Publisher> publishers = publisherRepository.findAllById(publisherIds);

        // Add all the publishers within the collection.
        publishers.forEach(game::addPublisher);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto updatePublishersForGameId(long id, @NonNull Collection<Long> publisherIds) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        // Get the game by the supplied ID, if it doesn't exist an exception will be thrown.
        var game = gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));

        // Get all of the matching publishers for the ID's provided.
        Iterable<Publisher> publishers = publisherRepository.findAllById(publisherIds);

        // Add all the publishers within the collection.
        publishers.forEach(game::addPublisher);

        // Save the game and return the result.
        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findGamesByFranchiseId(long franchiseId, Pageable pageable) {
        if (!franchiseRepository.existsById(franchiseId)) {
            String errorMessage = messageSource
                    .getMessage(FRANCHISE_NOT_FOUND_MESSAGE, new Object[] { franchiseId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByFranchiseId(franchiseId, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByFranchiseId(long franchiseId) {
        if (!franchiseRepository.existsById(franchiseId)) {
            String errorMessage = messageSource
                    .getMessage(FRANCHISE_NOT_FOUND_MESSAGE, new Object[] { franchiseId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByFranchiseId(franchiseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findAll() {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .map(gameMapper::fromGame)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameSpecification gameSpecification) {
        Objects.requireNonNull(gameSpecification);

        return gameRepository.count(gameSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto update(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (!gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        var game = gameMapper.toGame(gameDto);
        game.getAgeRatings().forEach(ageRating -> ageRating.setGame(game));
        game.getReleaseDates().forEach(gameReleaseDate -> gameReleaseDate.setGame(game));
        game.getDownloadableContents().forEach(downloadableContent -> downloadableContent.setGame(game));

        return gameMapper.fromGame(gameRepository.save(game));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameDto patched = patchService.patch(jsonMergePatch, findById(id), GameDto.class);
        // Save to the repository and convert it back to a GameDto.
        return gameMapper.fromGame(gameRepository.save(gameMapper.toGame(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        if (!gameRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        gameRepository.deleteById(id);
    }
}
