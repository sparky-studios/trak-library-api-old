package com.sparkystudios.traklibrary.authentication.service.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.mapstruct.factory.Mappers;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthMappers {

    public static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    public static final UserResponseMapper USER_RESPONSE_MAPPER = Mappers.getMapper(UserResponseMapper.class);
}
