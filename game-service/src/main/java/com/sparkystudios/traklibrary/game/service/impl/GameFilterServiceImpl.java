package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.*;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GameUserEntryRepository;
import com.sparkystudios.traklibrary.game.repository.GenreRepository;
import com.sparkystudios.traklibrary.game.repository.PlatformRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameSearchSpecification;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySearchSpecification;
import com.sparkystudios.traklibrary.game.service.GameFilterService;
import com.sparkystudios.traklibrary.game.service.dto.*;
import com.sparkystudios.traklibrary.game.service.mapper.GameDetailsMapper;
import com.sparkystudios.traklibrary.game.service.mapper.GameUserEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class GameFilterServiceImpl implements GameFilterService {

    private final PlatformRepository platformRepository;
    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final GameUserEntryRepository gameUserEntryRepository;
    private final GameDetailsMapper gameDetailsMapper;
    private final GameUserEntryMapper gameUserEntryMapper;

    @Override
    @Transactional(readOnly = true)
    public GameFiltersDto getGameFilters() {
        // Create the game filter to return to the callee.
        GameFiltersDto gameFiltersDto = new GameFiltersDto();

        // Retrieve every type of platform within the system and convert it into a filter.
        Set<GameFilterDto> platformFilters = StreamSupport.stream(platformRepository.findAll().spliterator(), false)
            .map(p -> {
                GameFilterDto gameFilterDto = new GameFilterDto();
                gameFilterDto.setName(p.getName());
                gameFilterDto.setId(p.getId());

                return gameFilterDto;
            })
            .collect(Collectors.toCollection(TreeSet::new));

        // Set the platform filters for the master filter.
        gameFiltersDto.setPlatforms(platformFilters);

        // Retrieve every type of genre within the system and convert it into a filter.
        Set<GameFilterDto> genreFilters = StreamSupport.stream(genreRepository.findAll().spliterator(), false)
                .map(p -> {
                    GameFilterDto gameFilterDto = new GameFilterDto();
                    gameFilterDto.setName(p.getName());
                    gameFilterDto.setId(p.getId());

                    return gameFilterDto;
                })
                .collect(Collectors.toCollection(TreeSet::new));

        // Set the genre filters for the master filter.
        gameFiltersDto.setGenres(genreFilters);

        return gameFiltersDto;
    }

    @Override
    @Transactional(readOnly = true)
    public GameUserEntryFiltersDto getGameUserEntryFilters() {
        // Create the game filter to return to the callee.
        GameUserEntryFiltersDto gameUserEntryFiltersDto = new GameUserEntryFiltersDto();

        // Retrieve every type of platform within the system and convert it into a filter.
        Set<GameFilterDto> platformFilters = StreamSupport.stream(platformRepository.findAll().spliterator(), false)
                .map(p -> {
                    GameFilterDto gameFilterDto = new GameFilterDto();
                    gameFilterDto.setName(p.getName());
                    gameFilterDto.setId(p.getId());

                    return gameFilterDto;
                })
                .collect(Collectors.toCollection(TreeSet::new));

        // Set the platform filters for the master filter.
        gameUserEntryFiltersDto.setPlatforms(platformFilters);

        // Retrieve every type of genre within the system and convert it into a filter.
        Set<GameFilterDto> genreFilters = StreamSupport.stream(genreRepository.findAll().spliterator(), false)
                .map(p -> {
                    GameFilterDto gameFilterDto = new GameFilterDto();
                    gameFilterDto.setName(p.getName());
                    gameFilterDto.setId(p.getId());

                    return gameFilterDto;
                })
                .collect(Collectors.toCollection(TreeSet::new));

        // Set the genre filters for the master filter.
        gameUserEntryFiltersDto.setGenres(genreFilters);

        return gameUserEntryFiltersDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDetailsDto> findGamesByFilters(Set<Long> platformIds,
                                                       Set<Long> genreIds,
                                                       Set<GameMode> gameModes,
                                                       Set<AgeRating> ageRatings,
                                                       Pageable pageable) {
        // Get the platforms from the filter query.
        Set<Platform> platforms = platformIds != null && !platformIds.isEmpty() ?
                StreamSupport.stream(platformRepository.findAllById(platformIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the genres from the filter query.
        Set<Genre> genres = genreIds != null && !genreIds.isEmpty() ?
                StreamSupport.stream(genreRepository.findAllById(genreIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the age ratings from the filter query.
        return gameRepository.findAll(new GameSearchSpecification(platforms, genres, gameModes, ageRatings), pageable)
                .map(gameDetailsMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGamesByFilters(Set<Long> platformIds,
                                    Set<Long> genreIds,
                                    Set<GameMode> gameModes,
                                    Set<AgeRating> ageRatings) {
        // Get the platforms from the filter query.
        Set<Platform> platforms = platformIds != null && !platformIds.isEmpty() ?
                StreamSupport.stream(platformRepository.findAllById(platformIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the genres from the filter query.
        Set<Genre> genres = genreIds != null && !genreIds.isEmpty() ?
                StreamSupport.stream(genreRepository.findAllById(genreIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        return gameRepository.count(new GameSearchSpecification(platforms, genres, gameModes, ageRatings));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameUserEntryDto> findGameUserEntriesByFilters(Set<Long> platformIds, Set<Long> genreIds, Set<GameMode> gameModes, Set<AgeRating> ageRatings, Set<GameUserEntryStatus> statuses, Pageable pageable) {
        // Get the platforms from the filter query.
        Set<Platform> platforms = platformIds != null && !platformIds.isEmpty() ?
                StreamSupport.stream(platformRepository.findAllById(platformIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the genres from the filter query.
        Set<Genre> genres = genreIds != null && !genreIds.isEmpty() ?
                StreamSupport.stream(genreRepository.findAllById(genreIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the age ratings from the filter query.
        return gameUserEntryRepository.findAll(new GameUserEntrySearchSpecification(platforms, genres, gameModes, ageRatings, statuses), pageable)
                .map(gameUserEntryMapper::fromGameUserEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGameUserEntriesByFilters(Set<Long> platformIds, Set<Long> genreIds, Set<GameMode> gameModes, Set<AgeRating> ageRatings, Set<GameUserEntryStatus> statuses) {
        // Get the platforms from the filter query.
        Set<Platform> platforms = platformIds != null && !platformIds.isEmpty() ?
                StreamSupport.stream(platformRepository.findAllById(platformIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        // Get the genres from the filter query.
        Set<Genre> genres = genreIds != null && !genreIds.isEmpty() ?
                StreamSupport.stream(genreRepository.findAllById(genreIds).spliterator(), false).collect(Collectors.toSet()) : Collections.emptySet();

        return gameUserEntryRepository.count(new GameUserEntrySearchSpecification(platforms, genres, gameModes, ageRatings, statuses));
    }
}
