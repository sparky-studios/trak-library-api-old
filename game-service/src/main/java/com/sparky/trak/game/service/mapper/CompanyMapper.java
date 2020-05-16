package com.sparky.trak.game.service.mapper;

import com.sparky.trak.game.domain.Company;
import com.sparky.trak.game.service.dto.CompanyDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDto companyToCompanyDto(Company company);

    Company companyDtoToCompany(CompanyDto companyDto);
}
