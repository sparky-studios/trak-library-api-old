package com.sparky.trak.authentication.service;

import com.sparky.trak.authentication.service.dto.CheckedResponse;
import com.sparky.trak.authentication.service.dto.RecoveryRequestDto;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import com.sparky.trak.authentication.service.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    CheckedResponse<UserResponseDto> save(RegistrationRequestDto registrationRequestDto);

    CheckedResponse<UserResponseDto> update(RecoveryRequestDto recoveryRequestDto);

    UserResponseDto findByUsername(String username);

    String createVerificationCode(String username);

    String createRecoveryToken(String username);

    CheckedResponse<Boolean> verify(String username, String verificationCode);

    void reverify(String username);

    void requestRecovery(String emailAddress);
}
