package com.traklibrary.game.service.impl;

import com.traklibrary.game.repository.DeveloperRepository;
import com.traklibrary.game.repository.GameDeveloperXrefRepository;
import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.specification.DeveloperSpecification;
import com.traklibrary.game.service.DeveloperService;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.dto.DeveloperDto;
import com.traklibrary.game.service.mapper.DeveloperMapper;
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

@RequiredArgsConstructor
@Service
public class DeveloperServiceImpl implements DeveloperService {

    private static final String ENTITY_EXISTS_MESSAGE = "developer.exception.entity-exists";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";
    private static final String NOT_FOUND_MESSAGE = "developer.exception.not-found";

    private final DeveloperRepository developerRepository;
    private final GameRepository gameRepository;
    private final GameDeveloperXrefRepository gameDeveloperXrefRepository;
    private final DeveloperMapper developerMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public DeveloperDto save(DeveloperDto developerDto) {
        Objects.requireNonNull(developerDto);

        if (developerRepository.existsById(developerDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { developerDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return developerMapper.developerToDeveloperDto(developerRepository.save(developerMapper.developerDtoToDeveloper(developerDto)));
    }

    @Override
    public DeveloperDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return developerMapper.developerToDeveloperDto(developerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<DeveloperDto> findDevelopersByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameDeveloperXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)))
                .stream()
                .map(xref -> developerMapper.developerToDeveloperDto(xref.getDeveloper()))
                .sorted(Comparator.comparing(DeveloperDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<DeveloperDto> findAll(DeveloperSpecification developerSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return developerRepository.findAll(developerSpecification, pageable)
                .map(developerMapper::developerToDeveloperDto);
    }

    @Override
    public long count(DeveloperSpecification developerSpecification) {
        Objects.requireNonNull(developerSpecification);

        return developerRepository.count(developerSpecification);
    }

    @Override
    public DeveloperDto update(DeveloperDto companyDto) {
        Objects.requireNonNull(companyDto);

        if (!developerRepository.existsById(companyDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { companyDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return developerMapper.developerToDeveloperDto(developerRepository.save(developerMapper.developerDtoToDeveloper(companyDto)));
    }

    @Override
    public DeveloperDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        DeveloperDto patched = patchService.patch(jsonMergePatch, findById(id), DeveloperDto.class);
        // Save to the repository and convert it back to a DeveloperDto.
        return developerMapper.developerToDeveloperDto(developerRepository.save(developerMapper.developerDtoToDeveloper(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!developerRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        developerRepository.deleteById(id);
    }
}
