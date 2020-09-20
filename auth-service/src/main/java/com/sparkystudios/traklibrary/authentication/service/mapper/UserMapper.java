package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }

        List<GrantedAuthority> authorities = user.getUserRoles()
                .stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole()))
                .collect(Collectors.toList());

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmailAddress(user.getEmailAddress());
        userDto.setPassword(user.getPassword());
        userDto.setVerified(user.isVerified());
        userDto.setVerificationCode(user.getVerificationCode());
        userDto.setVerificationExpiryDate(user.getVerificationExpiryDate());
        userDto.setVersion(user.getVersion());
        userDto.setAuthorities(authorities);

        return userDto;
    }

    @Mapping(target = "userRoles", ignore = true)
    User userDtoToUser(UserDto userDto);
}