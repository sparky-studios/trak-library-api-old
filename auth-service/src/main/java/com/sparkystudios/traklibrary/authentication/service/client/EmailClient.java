package com.sparkystudios.traklibrary.authentication.service.client;

import com.sparkystudios.traklibrary.authentication.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.EmailVerificationRequestDto;

public interface EmailClient {

    void sendVerificationEmail(EmailVerificationRequestDto emailVerificationRequestDto);

    void sendRecoveryEmail(EmailRecoveryRequestDto emailRecoveryRequestDto);

    void sendChangePasswordEmail(EmailRecoveryRequestDto emailRecoveryRequestDto);
}
