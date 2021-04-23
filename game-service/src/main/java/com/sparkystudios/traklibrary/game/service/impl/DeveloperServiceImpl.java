package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.domain.Game;
import com.sparkystudios.traklibrary.game.repository.DeveloperRepository;
import com.sparkystudios.traklibrary.game.repository.GameRepository;
import com.sparkystudios.traklibrary.game.repository.specification.DeveloperSpecification;
import com.sparkystudios.traklibrary.game.service.DeveloperService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.DeveloperDto;
import com.sparkystudios.traklibrary.game.service.mapper.DeveloperMapper;
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

@RequiredArgsConstructor
@Service
public class DeveloperServiceImpl implements DeveloperService {

    private static final String ENTITY_EXISTS_MESSAGE = "developer.exception.entity-exists";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";
    private static final String NOT_FOUND_MESSAGE = "developer.exception.not-found";

    private final DeveloperRepository developerRepository;
    private final GameRepository gameRepository;
    private final DeveloperMapper developerMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeveloperDto save(DeveloperDto developerDto) {
        Objects.requireNonNull(developerDto);

        if (developerRepository.existsById(developerDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { developerDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return developerMapper.fromDeveloper(developerRepository.save(developerMapper.toDeveloper(developerDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public DeveloperDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

        return developerMapper.fromDeveloper(developerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public DeveloperDto findBySlug(String slug) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { "slug", slug }, LocaleContextHolder.getLocale());

        return developerMapper.fromDeveloper(developerRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DeveloperDto> findDevelopersByGameId(long gameId) {
        // Get the game as the developers can be lazily loaded from it.
        Optional<Game> game = gameRepository.findById(gameId);

        if (game.isEmpty()) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        // Retrieve all associated developers and just convert them to their DTO counterparts.
        return game.get().getDevelopers().stream()
                .map(developerMapper::fromDeveloper)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<DeveloperDto> findAll(DeveloperSpecification developerSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return developerRepository.findAll(developerSpecification, pageable)
                .map(developerMapper::fromDeveloper);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(DeveloperSpecification developerSpecification) {
        Objects.requireNonNull(developerSpecification);

        return developerRepository.count(developerSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeveloperDto update(DeveloperDto companyDto) {
        Objects.requireNonNull(companyDto);

        if (!developerRepository.existsById(companyDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", companyDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return developerMapper.fromDeveloper(developerRepository.save(developerMapper.toDeveloper(companyDto)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeveloperDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        DeveloperDto patched = patchService.patch(jsonMergePatch, findById(id), DeveloperDto.class);
        // Save to the repository and convert it back to a DeveloperDto.
        return developerMapper.fromDeveloper(developerRepository.save(developerMapper.toDeveloper(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        if (!developerRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { "id", id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        developerRepository.deleteById(id);
    }
}
