package com.sparky.trak.game.service.impl;

import com.sparky.trak.game.repository.CompanyRepository;
import com.sparky.trak.game.repository.specification.CompanySpecification;
import com.sparky.trak.game.service.CompanyService;
import com.sparky.trak.game.service.PatchService;
import com.sparky.trak.game.service.dto.CompanyDto;
import com.sparky.trak.game.service.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final MessageSource messageSource;
    private final PatchService patchService;

    @Override
    public CompanyDto save(CompanyDto companyDto) {
        Objects.requireNonNull(companyDto);

        if (companyRepository.existsById(companyDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("company.exception.entity-exists", new Object[] { companyDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityExistsException(errorMessage);
        }

        return companyMapper.companyToCompanyDto(companyRepository.save(companyMapper.companyDtoToCompany(companyDto)));
    }

    @Override
    public CompanyDto findById(long id) {
        String errorMessage = messageSource
                .getMessage("company.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

        return companyMapper.companyToCompanyDto(companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(errorMessage)));
    }

    @Override
    public Iterable<CompanyDto> findAll(CompanySpecification companySpecification, Pageable pageable) {
        Objects.requireNonNull(pageable);

        return companyRepository.findAll(companySpecification, pageable)
                .map(companyMapper::companyToCompanyDto);
    }

    @Override
    public CompanyDto update(CompanyDto companyDto) {
        Objects.requireNonNull(companyDto);

        if (!companyRepository.existsById(companyDto.getId())) {
            String errorMessage = messageSource
                    .getMessage("company.exception.not-found", new Object[] { companyDto.getId() }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        return companyMapper.companyToCompanyDto(companyRepository.save(companyMapper.companyDtoToCompany(companyDto)));
    }

    @Override
    public CompanyDto patch(long id, JsonMergePatch jsonMergePatch) {
        // Set the new Java object with the patch information.
        CompanyDto patched = patchService.patch(jsonMergePatch, findById(id), CompanyDto.class);
        // Save to the repository and convert it back to a GameDto.
        return companyMapper.companyToCompanyDto(companyRepository.save(companyMapper.companyDtoToCompany(patched)));
    }

    @Override
    public void deleteById(long id) {
        if (!companyRepository.existsById(id)) {
            String errorMessage = messageSource
                    .getMessage("company.exception.not-found", new Object[] { id }, LocaleContextHolder.getLocale());

            throw new EntityNotFoundException(errorMessage);
        }

        companyRepository.deleteById(id);
    }
}
