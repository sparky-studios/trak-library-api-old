package com.traklibrary.game.service.impl;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.PlatformRepository;
import com.traklibrary.game.repository.specification.PlatformSpecification;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.PlatformService;
import com.traklibrary.game.service.dto.PlatformDto;
import com.traklibrary.game.service.mapper.PlatformMapper;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlatformServiceImpl implements PlatformService {

    private static final String ENTITY_EXISTS_MESSAGE = "platform.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "platform.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final PlatformRepository platformRepository;
    private final GameRepository gameRepository;
    private final PlatformMapper platformMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public PlatformDto save(PlatformDto platformDto) {
        Objects.requireNonNull(platformDto);

        if (platformRepository.existsById(platformDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { platformDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return platformMapper.platformToPlatformDto(platformRepository.save(platformMapper.platformDtoToPlatform(platformDto)));
    }

    @Override
    public PlatformDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return platformMapper.platformToPlatformDto(platformRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<PlatformDto> findPlatformsByGameId(long gameId) {
        // Get the game as the platforms can be lazily loaded from it.
        Optional<Game> game = gameRepository.findById(gameId);

        if (!game.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        // Retrieve all associated platforms and just convert them to their DTO counterparts.
        return game.get().getPlatforms().stream()
                .map(platformMapper::platformToPlatformDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PlatformDto> findAll(PlatformSpecification platformSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return platformRepository.findAll(platformSpecification, pageable)
                .map(platformMapper::platformToPlatformDto);
    }

    @Override
    public long count(PlatformSpecification platformSpecification) {
        Objects.requireNonNull(platformSpecification);

        return platformRepository.count(platformSpecification);
    }

    @Override
    public PlatformDto update(PlatformDto platformDto) {
        Objects.requireNonNull(platformDto);

        if (!platformRepository.existsById(platformDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { platformDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return platformMapper.platformToPlatformDto(platformRepository.save(platformMapper.platformDtoToPlatform(platformDto)));
    }

    @Override
    public PlatformDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        PlatformDto patched = patchService.patch(jsonMergePatch, findById(id), PlatformDto.class);
        // Save to the repository and convert it back to a GameDto.
        return platformMapper.platformToPlatformDto(platformRepository.save(platformMapper.platformDtoToPlatform(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!platformRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        platformRepository.deleteById(id);
    }
}
