package com.traklibrary.authentication.service.listener;

import com.google.common.base.Strings;
import com.traklibrary.authentication.service.UserService;
import com.traklibrary.authentication.service.client.EmailClient;
import com.traklibrary.authentication.service.event.OnRecoveryNeededEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RecoveryNeededEventListener implements ApplicationListener<OnRecoveryNeededEvent> {

    private final UserService userService;
    private final EmailClient emailClient;

    @Override
    public void onApplicationEvent(OnRecoveryNeededEvent onRecoveryNeededEvent) {
        // Creates and persists a new recovery token for the given user.
        String recoveryToken = userService.createRecoveryToken(onRecoveryNeededEvent.getUsername());
        // Only send the recovery token email if a new valid token has been created.
        if (!Strings.isNullOrEmpty(recoveryToken)) {
            emailClient.sendRecoveryEmail(onRecoveryNeededEvent.getEmailAddress(), recoveryToken);
        }
    }
}
