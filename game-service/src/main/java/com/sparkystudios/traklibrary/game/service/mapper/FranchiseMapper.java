package com.sparkystudios.traklibrary.game.service.mapper;

import com.github.slugify.Slugify;
import com.sparkystudios.traklibrary.game.domain.Franchise;
import com.sparkystudios.traklibrary.game.service.dto.FranchiseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    FranchiseDto fromFranchise(Franchise franchise);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "games", ignore = true)
    Franchise toFranchise(FranchiseDto franchiseDto);

    @AfterMapping
    default void afterMapping(@MappingTarget Franchise franchise) {
        franchise.setSlug(new Slugify().slugify(franchise.getTitle()));
    }
}
