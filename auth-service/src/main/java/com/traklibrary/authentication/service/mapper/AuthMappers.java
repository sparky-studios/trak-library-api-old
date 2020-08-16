package com.traklibrary.authentication.service.mapper;

import org.mapstruct.factory.Mappers;

public final class AuthMappers {

    public static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    public static final UserResponseMapper USER_RESPONSE_MAPPER = Mappers.getMapper(UserResponseMapper.class);
}
