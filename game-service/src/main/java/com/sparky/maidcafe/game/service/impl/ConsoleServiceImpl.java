package com.sparky.maidcafe.game.service.impl;

import com.sparky.maidcafe.game.repository.ConsoleRepository;
import com.sparky.maidcafe.game.repository.GameConsoleXrefRepository;
import com.sparky.maidcafe.game.repository.GameRepository;
import com.sparky.maidcafe.game.repository.specification.ConsoleSpecification;
import com.sparky.maidcafe.game.service.ConsoleService;
import com.sparky.maidcafe.game.service.PatchService;
import com.sparky.maidcafe.game.service.dto.ConsoleDto;
import com.sparky.maidcafe.game.service.dto.GenreDto;
import com.sparky.maidcafe.game.service.mapper.ConsoleMapper;
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
public class ConsoleServiceImpl implements ConsoleService {

    private final ConsoleRepository consoleRepository;
    private final GameRepository gameRepository;
    private final GameConsoleXrefRepository gameConsoleXrefRepository;
    private final ConsoleMapper consoleMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public ConsoleDto save(ConsoleDto consoleDto) {
        Objects.requireNonNull(consoleDto);

        if (consoleRepository.existsById(consoleDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("console.exception.entity-exists", new Object[] { consoleDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return consoleMapper.consoleToConsoleDto(consoleRepository.save(consoleMapper.consoleDtoToConsole(consoleDto)));
    }

    @Override
    public ConsoleDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("console.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return consoleMapper.consoleToConsoleDto(consoleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<ConsoleDto> findAll() {
        return StreamSupport.stream(consoleRepository.findAll().spliterator(), false)
                .map(consoleMapper::consoleToConsoleDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<ConsoleDto> findConsolesFromGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gameConsoleXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)))
                .stream()
                .map(xref -> consoleMapper.consoleToConsoleDto(xref.getConsole()))
                .sorted(Comparator.comparing(ConsoleDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<ConsoleDto> findAll(ConsoleSpecification consoleSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return consoleRepository.findAll(consoleSpecification, pageable)
                .map(consoleMapper::consoleToConsoleDto);
    }

    @Override
    public ConsoleDto update(ConsoleDto consoleDto) {
        Objects.requireNonNull(consoleDto);

        if (!consoleRepository.existsById(consoleDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("console.exception.not-found", new Object[] { consoleDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return consoleMapper.consoleToConsoleDto(consoleRepository.save(consoleMapper.consoleDtoToConsole(consoleDto)));
    }

    @Override
    public ConsoleDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        ConsoleDto patched = patchService.patch(jsonMergePatch, findById(id), ConsoleDto.class);
        // Save to the repository and convert it back to a GameDto.
        return consoleMapper.consoleToConsoleDto(consoleRepository.save(consoleMapper.consoleDtoToConsole(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!consoleRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage("console.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        consoleRepository.deleteById(id);
    }
}
