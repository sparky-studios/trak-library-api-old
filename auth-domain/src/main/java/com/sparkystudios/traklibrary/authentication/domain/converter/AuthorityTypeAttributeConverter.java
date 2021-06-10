package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.authentication.domain.AuthorityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AuthorityTypeAttributeConverter implements AttributeConverter<AuthorityType, Short> {

    @Override
    public Short convertToDatabaseColumn(AuthorityType authorityType) {
        return Objects.requireNonNullElse(authorityType, AuthorityType.READ).getId();
    }

    @Override
    public AuthorityType convertToEntityAttribute(Short authorityTypeId) {
        if (authorityTypeId == null) {
            return AuthorityType.READ;
        }

        return Stream.of(AuthorityType.values())
                .filter(at -> at.getId() == authorityTypeId)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
