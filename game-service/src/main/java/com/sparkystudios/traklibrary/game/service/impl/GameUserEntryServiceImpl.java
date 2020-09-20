package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.GameUserEntry;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.GameUserEntryRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameUserEntrySpecification;
import com.sparkystudios.traklibrary.game.service.GameUserEntryService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.GameUserEntryDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameUserEntryMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.exception.InvalidUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String INVALID_USER_MESSAGE = "game-user-entry.exception.invalid-user";
    private static final String ENTITY_EXISTS_MESSAGE = "game-user-entry.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "game-user-entry.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final GameUserEntryRepository gameUserEntryRepository;
    private final GameRepository gameRepository;
    private final GameUserEntryMapper gameUserEntryMapper;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameUserEntryDto save(GameUserEntryDto gameUserEntryDto) {
        Objects.requireNonNull(gameUserEntryDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (gameUserEntryRepository.existsById(gameUserEntryDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { gameUserEntryDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(gameUserEntryDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public GameUserEntryDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameUserEntryDto> findGameUserEntriesByGameId(long gameId, Pageable pageable) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameUserEntryRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)), pageable)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGameUserEntriesByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameUserEntryRepository
                .count((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameUserEntryDto> findAll(GameUserEntrySpecification gameUserEntrySpecification, Pageable pageable) {
        return StreamSupport.stream(gameUserEntryRepository.findAll(gameUserEntrySpecification, pageable).spliterator(), false)
                .map(gameUserEntryMapper::gameUserEntryToGameUserEntryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameUserEntrySpecification gameUserEntrySpecification) {
        Objects.requireNonNull(gameUserEntrySpecification);

        return gameUserEntryRepository.count(gameUserEntrySpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameUserEntryDto update(GameUserEntryDto gameUserEntryDto) {
        Objects.requireNonNull(gameUserEntryDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntryDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (!gameUserEntryRepository.existsById(gameUserEntryDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { gameUserEntryDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(gameUserEntryDto)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameUserEntryDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameUserEntryDto patched = patchService.patch(jsonMergePatch, findById(id), GameUserEntryDto.class);

        if (!authenticationService.isCurrentAuthenticatedUser(patched.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        // Save to the repository and convert it back to a GameDto.
        return gameUserEntryMapper.gameUserEntryToGameUserEntryDto(gameUserEntryRepository
                .save(gameUserEntryMapper.gameUserEntryDtoToGameUserEntry(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        Optional<GameUserEntry> gameUserEntry = gameUserEntryRepository.findById(id);

        if (!gameUserEntry.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        if (!authenticationService.isCurrentAuthenticatedUser(gameUserEntry.get().getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        gameUserEntryRepository.deleteById(id);
    }
}
