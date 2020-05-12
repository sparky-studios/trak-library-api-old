package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.repository.PlatformRepository;
import com.sparky.trak.game.repository.GamePlatformXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.specification.PlatformSpecification;
import com.sparky.trak.game.service.PlatformService;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.PlatformDto;
import com.sparky.trak.game.service.mapper.PlatformMapper;
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
public class PlatformServiceImpl implements PlatformService {

    private final PlatformRepository platformRepository;
    private final GameRepository gameRepository;
    private final GamePlatformXrefRepository gamePlatformXrefRepository;
    private final PlatformMapper platformMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public PlatformDto save(PlatformDto platformDto) {
        Objects.requireNonNull(platformDto);

        if (platformRepository.existsById(platformDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("platform.exception.entity-exists", new Object[] { platformDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return platformMapper.platformToPlatformDto(platformRepository.save(platformMapper.platformDtoToPlatform(platformDto)));
    }

    @Override
    public PlatformDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("platform.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return platformMapper.platformToPlatformDto(platformRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<PlatformDto> findAll() {
        return StreamSupport.stream(platformRepository.findAll().spliterator(), false)
                .map(platformMapper::platformToPlatformDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PlatformDto> findPlatformsFromGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePlatformXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)))
                .stream()
                .map(xref -> platformMapper.platformToPlatformDto(xref.getPlatform()))
                .sorted(Comparator.comparing(PlatformDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PlatformDto> findAll(PlatformSpecification platformSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return platformRepository.findAll(platformSpecification, pageable)
                .map(platformMapper::platformToPlatformDto);
    }

    @Override
    public PlatformDto update(PlatformDto platformDto) {
        Objects.requireNonNull(platformDto);

        if (!platformRepository.existsById(platformDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("platform.exception.not-found", new Object[] { platformDto.getId() }, LocaleContextHolder.getLocale());

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
                    .getMessage("platform.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        platformRepository.deleteById(id);
    }
}
