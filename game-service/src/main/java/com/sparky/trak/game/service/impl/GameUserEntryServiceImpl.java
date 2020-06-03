package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.domain.GameUserEntry;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.GameUserEntryRepository;
import com.sparky.trak.game.repository.specification.GameUserEntrySpecification;
import com.sparky.trak.game.service.AuthenticationService;
import com.sparky.trak.game.service.GameUserEntryService;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.GameUserEntryDto;
import com.sparky.trak.game.service.exception.InvalidUserException;
import com.sparky.trak.game.service.mapper.GameUserEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class GameUserEntryServiceImpl implements GameUserEntryService {

    private final GameUserEntryRepository gameUserEntryRepository;
    private final GameRepository gameRepository;
    private final GameUserEntryMapper gameUserEntryMapper;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public GameUserEntryDto save(GameUserEntryDto gameUserEntryDto) {
        Objects.requireNonNull(gameUserEntryDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.invalid-user", new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (gameUserEntryRepository.existsById(gameUserEntryDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.entity-exists", new Object[] { gameUserEntryDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(gameUserEntryDto)));
    }

    @Override
    public GameUserEntryDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("game-user-entry.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<GameUserEntryDto> findGameUserEntriesByGameId(long gameId, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameUserEntryRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)), pageable)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto);
    }

    @Override
    public long countGameUserEntriesByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameUserEntryRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId));
    }

    @Override
    public Iterable<GameUserEntryDto> findAll(GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable) {
        return StreamSupport.stream(gameUserEntryRepository.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto)
                .collect(Collectors.toList());
    }

    @Override
    public long count(GameUserEntrySpecification gameUserEntrySpecification) {
        Objects.requireNonNull(gameUserEntrySpecification);

        return gameUserEntryRepository.count(gameUserEntrySpecification);
    }

    @Override
    public GameUserEntryDto update(GameUserEntryDto gameUserEntryDto) {
        Objects.requireNonNull(gameUserEntryDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.invalid-user", new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (!gameUserEntryRepository.existsById(gameUserEntryDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.not-found", new Object[] { gameUserEntryDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(gameUserEntryDto)));
    }

    @Override
    public GameUserEntryDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameUserEntryDto patched = patchService.patch(jsonMergePatch, findById(id), GameUserEntryDto.class);

        if (!authenticationService.isCurrentAuthenticatedUser(patched.getUserId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.invalid-user", new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        // Save to the repository and convert it back to a GameDto.
        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(patched)));
    }

    @Override
    public void deleteById(long id) {
        Optional<GameUserEntry> gameUserEntry = gameUserEntryRepository.findById(id);

        if (!gameUserEntry.isPresent()) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntry.get().getUserId())) {
            String errorMessage = messageSource
                    .getMessage("game-user-entry.exception.invalid-user", new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        gameUserEntryRepository.deleteById(id);
    }
}
