package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GenreRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GenreSpecification;
import com.sparkystudios.traklibrary.game.service.GenreService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GenreDto;
import com.sparkystudios.traklibrary.game.service.mapper.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private static final String ENTITY_EXISTS_MESSAGE = "genre.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "genre.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final GenreMapper genreMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenreDto save(GenreDto genreDto) {
        Objects.requireNonNull(genreDto);

        if (genreRepository.existsById(genreDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { genreDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return genreMapper.fromGenre(genreRepository.save(genreMapper.toGenre(genreDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return genreMapper.fromGenre(genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GenreDto> findGenresByGameId(long gameId) {
        // Get the game as the developers can be lazily loaded from it.
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return game.get().getGenres()
                .stream()
                .map(genreMapper::fromGenre)
                .sorted(Comparator.comparing(GenreDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GenreDto> findAll(GenreSpecification genreSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return genreRepository.findAll(genreSpecification, pageable)
                .map(genreMapper::fromGenre);
}

    @Override
    @Transactional(readOnly = true)
    public long count(GenreSpecification genreSpecification) {
        Objects.requireNonNull(genreSpecification);

        return genreRepository.count(genreSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenreDto update(GenreDto genreDto) {
        Objects.requireNonNull(genreDto);

        if (!genreRepository.existsById(genreDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { genreDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return genreMapper.fromGenre(genreRepository.save(genreMapper.toGenre(genreDto)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenreDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GenreDto patched = patchService.patch(jsonMergePatch, findById(id), GenreDto.class);
        // Save to the repository and convert it back to a GameDto.
        return genreMapper.fromGenre(genreRepository.save(genreMapper.toGenre(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        if (!genreRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        genreRepository.deleteById(id);
    }
}
