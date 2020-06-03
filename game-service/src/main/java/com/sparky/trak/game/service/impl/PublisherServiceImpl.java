package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.repository.GamePublisherXrefRepository;
import com.sparky.trak.game.repository.GameRepository;
import com.sparky.trak.game.repository.PublisherRepository;
import com.sparky.trak.game.repository.specification.PublisherSpecification;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.PublisherService;
import com.sparky.trak.game.service.dto.PublisherDto;
import com.sparky.trak.game.service.mapper.PublisherMapper;
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
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final GameRepository gameRepository;
    private final GamePublisherXrefRepository gamePublisherXrefRepository;
    private final PublisherMapper publisherMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public PublisherDto save(PublisherDto publisherDto) {
        Objects.requireNonNull(publisherDto);

        if (publisherRepository.existsById(publisherDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("publisher.exception.entity-exists", new Object[] { publisherDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto)));
    }

    @Override
    public PublisherDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("publisher.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return publisherMapper.publisherToPublisherDto(publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<PublisherDto> findPublishersByGameId(long gameId) {
        if (!gameRepository.existsById(gameId)) {
            String errorMessage = messageSource
                    .getMessage("game.exception.not-found", new Object[] { gameId }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException((errorMessage));
        }

        return gamePublisherXrefRepository
                .findAll(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("gameId"), gameId)))
                .stream()
                .map(xref -> publisherMapper.publisherToPublisherDto(xref.getPublisher()))
                .sorted(Comparator.comparing(PublisherDto::getName))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<PublisherDto> findAll(PublisherSpecification publisherSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return publisherRepository.findAll(publisherSpecification, pageable)
                .map(publisherMapper::publisherToPublisherDto);
    }

    @Override
    public long count(PublisherSpecification publisherSpecification) {
        Objects.requireNonNull(publisherSpecification);

        return publisherRepository.count(publisherSpecification);
    }

    @Override
    public PublisherDto update(PublisherDto publisherDto) {
        Objects.requireNonNull(publisherDto);

        if (!publisherRepository.existsById(publisherDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("publisher.exception.not-found", new Object[] { publisherDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(publisherDto)));
    }

    @Override
    public PublisherDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        PublisherDto patched = patchService.patch(jsonMergePatch, findById(id), PublisherDto.class);
        // Save to the repository and convert it back to a PublisherDto.
        return publisherMapper.publisherToPublisherDto(publisherRepository.save(publisherMapper.publisherDtoToPublisher(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!publisherRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage("publisher.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        publisherRepository.deleteById(id);
    }
}
