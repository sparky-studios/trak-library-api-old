package com.sparkystudios.traklibrary.authentication.service.listener;

import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.client.EmailClient;
import com.sparkystudios.traklibrary.authentication.service.event.OnVerificationNeededEvent;
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
        if (!Strings.isNullOrEmpty(verificationCode)) {
            emailClient.sendVerificationEmail(onVerificationNeededEvent.getEmailAddress(), verificationCode);
        }
    }
}
