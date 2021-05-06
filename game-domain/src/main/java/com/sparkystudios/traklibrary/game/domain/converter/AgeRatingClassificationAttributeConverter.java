package com.sparkystudios.traklibrary.game.domain.converter;

import com.sparkystudios.traklibrary.game.domain.AgeRatingClassification;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AgeRatingClassificationAttributeConverter implements AttributeConverter<AgeRatingClassification, Short> {

    @Override
    public Short convertToDatabaseColumn(AgeRatingClassification ageRatingClassification) {
        return Objects.requireNonNullElse(ageRatingClassification, AgeRatingClassification.ESRB).getId();
    }

    @Override
    public AgeRatingClassification convertToEntityAttribute(Short ageRatingClassificationId) {
        if (ageRatingClassificationId == null) {
            return AgeRatingClassification.ESRB;
        }

        return Stream.of(AgeRatingClassification.values())
                .filter(ag -> ag.getId() == ageRatingClassificationId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
