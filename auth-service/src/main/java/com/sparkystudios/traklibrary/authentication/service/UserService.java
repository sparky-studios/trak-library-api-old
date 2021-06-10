package com.sparkystudios.traklibrary.authentication.service;

import com.sparkystudios.traklibrary.authentication.service.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    CheckedResponse<RegistrationResponseDto> save(RegistrationRequestDto registrationRequestDto);

    CheckedResponse<UserResponseDto> update(RecoveryRequestDto recoveryRequestDto);

    void deleteById(long id);

    CheckedResponse<Boolean> verify(long id, String verificationCode);

    void reverify(long id);

    void requestRecovery(String emailAddress);

    CheckedResponse<Boolean> changePassword(long id, ChangePasswordRequestDto changePasswordRequestDto);

    CheckedResponse<Boolean> changeEmailAddress(long id, String emailAddress);
}
