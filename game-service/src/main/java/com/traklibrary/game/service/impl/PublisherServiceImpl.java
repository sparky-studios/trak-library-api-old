package com.traklibrary.game.service.impl;

import com.traklibrary.game.domain.Game;
import com.traklibrary.game.repository.GameRepository;
import com.traklibrary.game.repository.PublisherRepository;
import com.traklibrary.game.repository.specification.PublisherSpecification;
import com.traklibrary.game.service.PatchService;
import com.traklibrary.game.service.PublisherService;
import com.traklibrary.game.service.dto.PublisherDto;
import com.traklibrary.game.service.mapper.PublisherMapper;
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
public class PublisherServiceImpl implements PublisherService {

    private static final String ENTITY_EXISTS_MESSAGE = "publisher.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "publisher.exception.not-found";
    private static final String GAME_NOT_FOUND_MESSAGE = "game.exception.not-found";

    private final PublisherRepository publisherRepository;
    private final GameRepository gameRepository;
    private final PublisherMapper publisherMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherDto save(PublisherDto publisherDto) {
        Objects.requireNonNull(publisherDto);

        if (publisherRepository.existsById(publisherDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { publisherDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public PublisherDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return publisherMapper.publisherToPublisherDto(publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PublisherDto> findPublishersByGameId(long gameId) {
        // Get the game as the publishers can be lazily loaded from it.
        Optional<Game> game = gameRepository.findById(gameId);

        if (!game.isPresent()) {
            String errorMessage = messageSource
                    .getMessage(GAME_NOT_FOUND_MESSAGE, new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        // Retrieve all associated developers and just convert them to their DTO counterparts.
        return game.get().getPublishers().stream()
                .map(publisherMapper::publisherToPublisherDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<PublisherDto> findAll(PublisherSpecification publisherSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return publisherRepository.findAll(publisherSpecification, pageable)
                .map(publisherMapper::publisherToPublisherDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(PublisherSpecification publisherSpecification) {
        Objects.requireNonNull(publisherSpecification);

        return publisherRepository.count(publisherSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherDto update(PublisherDto publisherDto) {
        Objects.requireNonNull(publisherDto);

        if (!publisherRepository.existsById(publisherDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { publisherDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublisherDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        PublisherDto patched = patchService.patch(jsonMergePatch, findById(id), PublisherDto.class);
        // Save to the repository and convert it back to a PublisherDto.
        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        if (!publisherRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        publisherRepository.deleteById(id);
    }
}
