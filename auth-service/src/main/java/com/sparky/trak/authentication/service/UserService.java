package com.sparky.trak.authentication.service;

import com.sparky.trak.authentication.service.dto.CheckedResponse;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    CheckedResponse<UserResponseDto> save(RegistrationRequestDto registrationRequestDto);

    UserResponseDto findByUsername(String username);

    void verify(String username, short verificationCode);
}
