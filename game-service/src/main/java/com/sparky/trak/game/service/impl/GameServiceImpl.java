package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.repository.*;
import com.sparky.trak.game.repository.specification.GameSpecification;
import com.sparky.trak.game.service.GameService;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.GameDto;
import com.sparky.trak.game.service.mapper.GameMapper;
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

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final GameGenreXrefRepository gameGenreXrefRepository;
    private final ConsoleRepository consoleRepository;
    private final GameConsoleXrefRepository gameConsoleXrefRepository;
    private final GameMapper gameMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public GameDto save(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game.exception.entity-exists", new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameMapper.gameToGameDto(gameRepository.save(gameMapper.gameDtoToGame(gameDto)));
    }

    @Override
    public GameDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("game.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return gameMapper.gameToGameDto(gameRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GameDto> findGamesByGenreId(long genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            String errorMessage = messageSource
                    .getMessage("genre.exception.not-found", new Object[] { genreId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameGenreXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("genreId"), genreId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
    }

    @Override
    public Iterable<GameDto> findGamesByConsoleId(long consoleId, Pageable pageable) {
        if (!consoleRepository.existsById(consoleId)) {
            String errorMessage = messageSource
                    .getMessage("console.exception.not-found", new Object[] { consoleId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameConsoleXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("consoleId"), consoleId)), pageable)
                .map(xref -> gameMapper.gameToGameDto(xref.getGame()));
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
    public GameDto update(GameDto gameDto) {
        Objects.requireNonNull(gameDto);

        if (!gameRepository.existsById(gameDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameDto.getId() }, LocaleContextHolder.getLocale());

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
                    .getMessage("game.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        gameRepository.deleteById(id);
    }
}
