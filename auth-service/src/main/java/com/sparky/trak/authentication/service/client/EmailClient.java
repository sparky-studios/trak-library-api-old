package com.sparky.trak.authentication.service.client;

public interface EmailClient {

    void sendVerificationEmail(String emailAddress, String verificationCode);
}
