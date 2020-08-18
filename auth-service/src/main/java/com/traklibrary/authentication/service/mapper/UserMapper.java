package com.traklibrary.authentication.service.mapper;

import com.traklibrary.authentication.domain.User;
import com.traklibrary.authentication.domain.UserRoleXref;
import com.traklibrary.authentication.service.dto.UserDto;
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

        List<GrantedAuthority> authorities = user.getUserRoleXrefs()
                .stream()
                .map(UserRoleXref::getUserRole)
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

    @Mapping(target = "userRoleXrefs", ignore = true)
    User userDtoToUser(UserDto userDto);
}