package com.sparky.trak.game.domain.converter;

import com.sparky.trak.game.domain.AgeRating;
import com.sparky.trak.game.domain.CompanyType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class CompanyTypeAttributeConverter implements AttributeConverter<CompanyType, Short> {

    @Override
    public Short convertToDatabaseColumn(CompanyType companyType) {
        if (companyType == null) {
            return CompanyType.PUBLISHER.getId();
        }

        return companyType.getId();
    }

    @Override
    public CompanyType convertToEntityAttribute(Short companyTypeId) {
        if (companyTypeId == null) {
            return CompanyType.PUBLISHER;
        }

        return Stream.of(CompanyType.values())
                .filter(ag -> ag.getId() == companyTypeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
