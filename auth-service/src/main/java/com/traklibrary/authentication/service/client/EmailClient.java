package com.traklibrary.authentication.service.client;

public interface EmailClient {

    void sendVerificationEmail(String emailAddress, String verificationCode);

    void sendRecoveryEmail(String emailAddress, String recoveryToken);
}
