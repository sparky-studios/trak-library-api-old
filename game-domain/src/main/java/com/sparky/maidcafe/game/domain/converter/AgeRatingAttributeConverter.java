package com.sparky.maidcafe.game.domain.converter;

import com.sparky.maidcafe.game.domain.AgeRating;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AgeRatingAttributeConverter implements AttributeConverter<AgeRating, Short> {

    @Override
    public Short convertToDatabaseColumn(AgeRating ageRating) {
        if (ageRating == null) {
            return AgeRating.RATING_PENDING.getId();
        }

        return ageRating.getId();
    }

    @Override
    public AgeRating convertToEntityAttribute(Short ageRatingId) {
        if (ageRatingId == null) {
            return AgeRating.RATING_PENDING;
        }

        return Stream.of(AgeRating.values())
                .filter(ag -> ag.getId() == ageRatingId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
