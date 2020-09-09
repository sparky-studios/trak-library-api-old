package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.GenreRepository;
import com.traklibrary.game.repository.specification.GameSpecification;
import com.traklibrary.game.service.GameInfoService;
import com.traklibrary.game.service.dto.GameInfoDto;
import com.traklibrary.game.service.mapper.GameInfoMapper;
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
public class GameInfoServiceImpl implements GameInfoService {

    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";
    private static final String GENRE_NOT_FOUND_MESSAGE = "genre.exception.not-found";

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameInfoMapper gameInfoMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public GameInfoDto findByGameId(long gameId) {
        String errorMessage = messageSource
                .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

        return gameInfoMapper.gameToGameInfoDto(gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameInfoDto> findByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage(GENRE_NOT_FOUND_MESSAGE, new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameRepository.findByGenresId(genreId, pageable)
                .map(gameInfoMapper::gameToGameInfoDto);
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
    public Iterable<GameInfoDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameInfoMapper::gameToGameInfoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameSpecification gameSpecification) {
        Objects.requireNonNull(gameSpecification);

        return gameRepository.count(gameSpecification);
    }
}
