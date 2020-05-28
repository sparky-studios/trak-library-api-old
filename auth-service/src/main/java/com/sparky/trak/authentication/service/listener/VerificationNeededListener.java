package com.sparky.trak.authentication.service.listener;

import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.client.EmailClient;
import com.sparky.trak.authentication.service.event.OnVerificationNeededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class VerificationNeededListener implements ApplicationListener<OnVerificationNeededEvent> {

    private final UserService userService;
    private final EmailClient emailClient;

    @Override
    public void onApplicationEvent(OnVerificationNeededEvent onVerificationNeededEvent) {
        // Creates and persists a new verification code for the given user.
        String verificationCode = userService.createVerificationCode(onVerificationNeededEvent.getUsername());
        // Only send a new email if a new valid has been created.
        if (!verificationCode.equals("")) {
            emailClient.sendVerificationEmail(onVerificationNeededEvent.getEmailAddress(), verificationCode);
        }
    }
}
