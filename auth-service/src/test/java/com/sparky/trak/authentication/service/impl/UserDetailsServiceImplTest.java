package com.sparky.trak.authentication.service.impl;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.repository.UserRepository;
import com.sparky.trak.authentication.service.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void loadUserByUsername_withNonExistentUser_throwsUsernameNotFoundException() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(""));
    }

    @Test
    public void loadUserByUsername_withExistingUser_returnsMappedUserDto() {
        // Arrange
        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new User()));

        // Act
        userDetailsService.loadUserByUsername("");

        // Assert
        Mockito.verify(userMapper, Mockito.atMostOnce())
                .userToUserDto(ArgumentMatchers.any());
    }
}
