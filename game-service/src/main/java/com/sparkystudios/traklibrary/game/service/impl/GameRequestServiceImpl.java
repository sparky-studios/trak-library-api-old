package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.GameRequest;
import com.sparkystudios.traklibrary.game.repository.GameRequestRepository;
import com.sparkystudios.traklibrary.game.repository.specification.GameRequestSpecification;
import com.sparkystudios.traklibrary.game.service.GameRequestService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.client.NotificationClient;
import com.sparkystudios.traklibrary.game.service.dto.GameRequestDto;
import com.sparkystudios.traklibrary.game.service.mapper.GameRequestMapper;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.exception.InvalidUserException;
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
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GameRequestServiceImpl implements GameRequestService {

    private static final String INVALID_USER_MESSAGE = "game-request.exception.invalid-user";
    private static final String ENTITY_EXISTS_MESSAGE = "game-request.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "game-request.exception.not-found";

    private final GameRequestRepository gameRequestRepository;
    private final GameRequestMapper gameRequestMapper;
    private final AuthenticationService authenticationService;
    private final MessageSource messageSource;
    private final PatchService patchService;
    private final NotificationClient notificationClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameRequestDto save(GameRequestDto gameRequestDto) {
        Objects.requireNonNull(gameRequestDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameRequestDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (gameRequestRepository.existsById(gameRequestDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { gameRequestDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return gameRequestMapper.fromGameRequest(gameRequestRepository.save(gameRequestMapper.toGameRequest(gameRequestDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public GameRequestDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return gameRequestMapper.fromGameRequest(gameRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<GameRequestDto> findAll(GameRequestSpecification gameRequestSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return gameRequestRepository.findAll(gameRequestSpecification, pageable)
                .map(gameRequestMapper::fromGameRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(GameRequestSpecification gameRequestSpecification) {
        Objects.requireNonNull(gameRequestSpecification);

        return gameRequestRepository.count(gameRequestSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameRequestDto update(GameRequestDto gameRequestDto) {
        Objects.requireNonNull(gameRequestDto);

        if (!authenticationService.isCurrentAuthenticatedUser(gameRequestDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        if (!gameRequestRepository.existsById(gameRequestDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { gameRequestDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return gameRequestMapper.fromGameRequest(gameRequestRepository.save(gameRequestMapper.toGameRequest(gameRequestDto)));
    }

    @Override
    public void complete(@NonNull GameRequestDto gameRequestDto) {
        // Only complete the request if it's not already flagged as completed.
        if (!gameRequestDto.isCompleted()) {
            gameRequestDto.setCompleted(true);
            gameRequestDto.setCompletedDate(LocalDateTime.now());

            // Save to the underlying persistence layer.
            gameRequestRepository.save(gameRequestMapper.toGameRequest(gameRequestDto));

            // Grab the localized text for the notification.
            String title = messageSource
                    .getMessage("game-request.notification.complete.title", new Object[]{}, LocaleContextHolder.getLocale());

            String content = messageSource
                    .getMessage("game-request.notification.complete.content", new Object[] {gameRequestDto.getTitle()}, LocaleContextHolder.getLocale());

            // Dispatch the notification.
            notificationClient.send(gameRequestDto.getUserId(), title, content);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameRequestDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        GameRequestDto patched = patchService.patch(jsonMergePatch, findById(id), GameRequestDto.class);

        if (!authenticationService.isCurrentAuthenticatedUser(patched.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        // Save to the repository and convert it back to a GameDto.
        return gameRequestMapper.fromGameRequest(gameRequestRepository.save(gameRequestMapper.toGameRequest(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        Optional<GameRequest> gameRequest = gameRequestRepository.findById(id);

        if (!gameRequest.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        if (!authenticationService.isCurrentAuthenticatedUser(gameRequest.get().getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        gameRequestRepository.deleteById(id);
    }
}
