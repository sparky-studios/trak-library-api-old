package com.sparkystudios.traklibrary.authentication.service;

import com.sparkystudios.traklibrary.authentication.service.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    CheckedResponse<UserResponseDto> save(RegistrationRequestDto registrationRequestDto);

    CheckedResponse<UserResponseDto> update(RecoveryRequestDto recoveryRequestDto);

    void deleteByUsername(String username);

    CheckedResponse<Boolean> verify(String username, String verificationCode);

    void reverify(String username);

    void requestRecovery(String emailAddress);

    void requestChangePassword(String username);

    CheckedResponse<Boolean> changePassword(String username, ChangePasswordRequestDto changePasswordRequestDto);

    CheckedResponse<Boolean> changeEmailAddress(String username, String emailAddress);
}
