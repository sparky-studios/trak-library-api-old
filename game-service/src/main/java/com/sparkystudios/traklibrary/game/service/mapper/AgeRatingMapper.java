package com.sparkystudios.traklibrary.game.service.mapper;

import com.sparkystudios.traklibrary.game.domain.AgeRating;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AgeRatingMapper {

    AgeRatingDto fromAgeRating(AgeRating ageRating);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "game", ignore = true)
    AgeRating toAgeRating(AgeRatingDto ageRatingDto);
}
