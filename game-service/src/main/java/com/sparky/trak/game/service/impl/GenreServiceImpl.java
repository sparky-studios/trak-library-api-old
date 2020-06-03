package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.repository.GameGenreXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.GenreRepository;
import com.sparky.trak.game.repository.specification.GenreSpecification;
import com.sparky.trak.game.service.GenreService;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.GenreDto;
import com.sparky.trak.game.service.mapper.GenreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GameRepository gameRepository;
    private final GameGenreXrefRepository gameGenreXrefRepository;
    private final GenreMapper genreMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public GenreDto save(GenreDto genreDto) {
        Objects.requireNonNull(genreDto);

        if (genreRepository.existsById(genreDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.entity-exists", new Object[] { genreDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return genreMapper.genreToGenreDto(genreRepository.save(genreMapper.genreDtoToGenre(genreDto)));
    }

    @Override
    public GenreDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("genre.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return genreMapper.genreToGenreDto(genreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GenreDto> findGenresByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)))
                .stream()
                .map(xref -> genreMapper.genreToGenreDto(xref.getGenre()))
                .sorted(Comparator.comparing(GenreDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<GenreDto> findAll(GenreSpecification genreSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return genreRepository.findAll(genreSpecification, pageable)
                .map(genreMapper::genreToGenreDto);
}

    @Override
    public long count(GenreSpecification genreSpecification) {
        Objects.requireNonNull(genreSpecification);

        return genreRepository.count(genreSpecification);
    }

    @Override
    public GenreDto update(GenreDto genreDto) {
        Objects.requireNonNull(genreDto);

        if (!genreRepository.existsById(genreDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.not-found", new Object[] { genreDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return genreMapper.genreToGenreDto(genreRepository.save(genreMapper.genreDtoToGenre(genreDto)));
    }

    @Override
    public GenreDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GenreDto patched = patchService.patch(jsonMergePatch, findById(id), GenreDto.class);
        // Save to the repository and convert it back to a GameDto.
        return genreMapper.genreToGenreDto(genreRepository.save(genreMapper.genreDtoToGenre(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!genreRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        genreRepository.deleteById(id);
    }
}
