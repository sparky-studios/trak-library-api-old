package com.sparky.trak.authentication.service.mapper;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    UserResponseMapper INSTANCE = Mappers.getMapper(UserResponseMapper.class);

    UserResponseDto userToUserResponseDto(User user);
}
