package com.sparkystudios.traklibrary.authentication.domain.converter;

import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Objects;

@Converter(autoApply = true)
public class UserSecurityRoleAttributeConverter implements AttributeConverter<UserSecurityRole, String> {

    @Override
    public String convertToDatabaseColumn(UserSecurityRole userSecurityRole) {
        return Objects.requireNonNull(userSecurityRole).name();
    }

    @Override
    public UserSecurityRole convertToEntityAttribute(String name) {
        return UserSecurityRole.valueOf(name);
    }
}
