package com.sparkystudios.traklibrary.game.service.impl;

import com.sparkystudios.traklibrary.game.repository.FranchiseRepository;
import com.sparkystudios.traklibrary.game.repository.specification.FranchiseSpecification;
import com.sparkystudios.traklibrary.game.service.FranchiseService;
import com.sparkystudios.traklibrary.game.service.PatchService;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import com.sparkystudios.traklibrary.game.service.mapper.FranchiseMapper;
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

@RequiredArgsConstructor
@Service
public class FranchiseServiceImpl implements FranchiseService {

    private static final String ENTITY_EXISTS_MESSAGE = "franchise.exception.entity-exists";
    private static final String NOT_FOUND_MESSAGE = "franchise.exception.not-found";

    private final FranchiseRepository franchiseRepository;
    private final FranchiseMapper franchiseMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FranchiseDto save(FranchiseDto franchiseDto) {
        Objects.requireNonNull(franchiseDto);

        if (franchiseRepository.existsById(franchiseDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(ENTITY_EXISTS_MESSAGE, new Object[] { franchiseDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return franchiseMapper.franchiseToFranchiseDto(franchiseRepository.save(franchiseMapper.franchiseDtoToFranchise(franchiseDto)));
    }

    @Override
    @Transactional(readOnly = true)
    public FranchiseDto findById(long id) {
        String errorMessage = messageSource
                .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

        return franchiseMapper.franchiseToFranchiseDto(franchiseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<FranchiseDto> findAll(FranchiseSpecification franchiseSpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return franchiseRepository.findAll(franchiseSpecification, pageable)
                .map(franchiseMapper::franchiseToFranchiseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long count(FranchiseSpecification franchiseSpecification) {
        Objects.requireNonNull(franchiseSpecification);

        return franchiseRepository.count(franchiseSpecification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FranchiseDto update(FranchiseDto franchiseDto) {
        Objects.requireNonNull(franchiseDto);

        if (!franchiseRepository.existsById(franchiseDto.getId())) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { franchiseDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return franchiseMapper.franchiseToFranchiseDto(franchiseRepository.save(franchiseMapper.franchiseDtoToFranchise(franchiseDto)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FranchiseDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        FranchiseDto patched = patchService.patch(jsonMergePatch, findById(id), FranchiseDto.class);
        // Save to the repository and convert it back to a GameDto.
        return franchiseMapper.franchiseToFranchiseDto(franchiseRepository.save(franchiseMapper.franchiseDtoToFranchise(patched)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        if (!franchiseRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage(NOT_FOUND_MESSAGE, new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        franchiseRepository.deleteById(id);
    }
}
