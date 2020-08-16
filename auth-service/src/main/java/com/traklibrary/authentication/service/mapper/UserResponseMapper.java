package com.traklibrary.authentication.service.mapper;

import com.traklibrary.authentication.domain.User;
import com.traklibrary.authentication.service.dto.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {

    UserResponseDto userToUserResponseDto(User user);
}
