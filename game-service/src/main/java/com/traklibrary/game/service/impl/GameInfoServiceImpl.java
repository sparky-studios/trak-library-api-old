package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.GameGenreXrefRepository;
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

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class GameInfoServiceImpl implements GameInfoService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreXrefRepository gameGenreXrefRepository;
    private final GameInfoMapper gameInfoMapper;
    private final MessageSource messageSource;

    @Override
    public GameInfoDto findByGameId(long gameId) {
        String errorMessage = messageSource
                .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

        return gameInfoMapper.gameToGameInfoDto(gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GameInfoDto> findByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.not-found", new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("genreId"), genreId)), pageable)
                .map(xref -> gameInfoMapper.gameToGameInfoDto(xref.getGame()));
    }

    @Override
    public long countByGenreId(long genreId) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.not-found", new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("genreId"), genreId));
    }

    @Override
    public Iterable<GameInfoDto> findAll(GameSpecification gameSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRepository.findAll(gameSpecification, pageable)
                .map(gameInfoMapper::gameToGameInfoDto);
    }

    @Override
    public long count(GameSpecification gameSpecification) {
        Objects.requireNonNull(gameSpecification);

        return gameRepository.count(gameSpecification);
    }
}
