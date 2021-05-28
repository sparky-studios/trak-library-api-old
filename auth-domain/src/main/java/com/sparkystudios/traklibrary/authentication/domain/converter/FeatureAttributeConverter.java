package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.authentication.domain.Feature;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class FeatureAttributeConverter implements AttributeConverter<Feature, Short> {

    @Override
    public Short convertToDatabaseColumn(Feature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be persisted if null.");
        }

        return feature.getId();
    }

    @Override
    public Feature convertToEntityAttribute(Short featureId) {
        if (featureId == null) {
            throw new IllegalArgumentException("Feature cannot be persisted if null.");
        }

        return Stream.of(Feature.values())
                .filter(f -> f.getId() == featureId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
