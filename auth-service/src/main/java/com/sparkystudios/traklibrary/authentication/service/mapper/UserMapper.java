package com.sparkystudios.traklibrary.authentication.service.mapper;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "authorities", ignore = true)
    UserDto fromUser(User user);

    @Mapping(target = "userRole", ignore = true)
    @Mapping(target = "authUserRoleId", ignore = true)
    @Mapping(target = "userAuthorities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(UserDto userDto);

    @AfterMapping
    default void afterMapping(@MappingTarget UserDto userDto, User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (user.getUserRole() != null) {
            authorities.add(new SimpleGrantedAuthority(user.getUserRole().getRole().name()));
        }

        user.getUserAuthorities()
                .forEach(a -> authorities.add(new SimpleGrantedAuthority(a.getAuthority())));

        userDto.setAuthorities(authorities);
    }
}