package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }

        var userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmailAddress(user.getEmailAddress());
        userDto.setPassword(user.getPassword());
        userDto.setVerified(user.isVerified());
        userDto.setVerificationCode(user.getVerificationCode());
        userDto.setVerificationExpiryDate(user.getVerificationExpiryDate());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        userDto.setVersion(user.getVersion());

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getUserRole() != null) {
            authorities.add(new SimpleGrantedAuthority(user.getUserRole().getRole()));
        }

        user.getAuthorities()
                .forEach(a -> authorities.add(new SimpleGrantedAuthority(a.getAuthority())));

        userDto.setAuthorities(authorities);
        return userDto;
    }

    @Mapping(target = "userRole", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User userDtoToUser(UserDto userDto);
}