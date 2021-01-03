package com.sparkystudios.traklibrary.authentication.service.listener;

import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.client.EmailClient;
import com.sparkystudios.traklibrary.authentication.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.authentication.service.event.OnChangePasswordEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChangePasswordEventListener implements ApplicationListener<OnChangePasswordEvent> {

    private final UserService userService;
    private final EmailClient emailClient;

    @Override
    public void onApplicationEvent(OnChangePasswordEvent onChangePasswordEvent) {
        // Creates and persists a new recovery token for the given user.
        String recoveryToken = userService.createRecoveryToken(onChangePasswordEvent.getUsername());
        // Only send the recovery token email if a new valid token has been created.
        if (!Strings.isNullOrEmpty(recoveryToken)) {
            EmailRecoveryRequestDto emailRecoveryRequestDto = new EmailRecoveryRequestDto();
            emailRecoveryRequestDto.setEmailAddress(onChangePasswordEvent.getEmailAddress());
            emailRecoveryRequestDto.setRecoveryToken(recoveryToken);

            emailClient.sendChangePasswordEmail(emailRecoveryRequestDto);
        }
    }
}
