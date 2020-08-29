package com.traklibrary.authentication.service;

import com.traklibrary.authentication.service.dto.*;
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

    void requestChangePassword(String username);

    CheckedResponse<Boolean> changePassword(ChangePasswordRequestDto changePasswordRequestDto);
}
