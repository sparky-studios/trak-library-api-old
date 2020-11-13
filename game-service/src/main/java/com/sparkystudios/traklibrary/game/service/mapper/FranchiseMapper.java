package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    FranchiseDto franchiseToFranchiseDto(Franchise franchise);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "games", ignore = true)
    Franchise franchiseDtoToFranchise(FranchiseDto franchiseDto);
}
