package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GenreRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameSpecification;
import com.sparkystudios.traklibrary.game.service.GameDetailsService;
import com.sparkystudios.traklibrary.game.service.dto.GameDetailsDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class GameDetailsServiceImpl implements GameDetailsService {

    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";
    private static final String GENRE_NOT_FOUND_MESSAGE = "genre.exception.not-found";

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameDetailsMapper gameDetailsMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public GameDetailsDto findByGameId(long gameId) {
        String errorMessage = messageSource
                .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { "id", gameId }, LocaleContextHolder.getLocale());

        return gameDetailsMapper.fromGame(gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public GameDetailsDto findByGameSlug(String slug) {
        String errorMessage = messageSource
                .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { "slug", slug }, LocaleContextHolder.getLocale());

        return gameDetailsMapper.fromGame(gameRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDetailsDto> findByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByGenresId(genreId, pageable)
                .map(gameDetailsMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByGenreId(long genreId) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.countByGenresId(genreId);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameDetailsDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameDetailsMapper::fromGame);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameSpecification gameSpecification) {
        Objects.requireNonNull(gameSpecification);

        return gameRepository.count(gameSpecification);
    }
}
