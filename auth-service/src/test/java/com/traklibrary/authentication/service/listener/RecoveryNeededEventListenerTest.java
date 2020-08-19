package com.traklibrary.authentication.service.listener;

import com.traklibrary.authentication.service.UserService;
import com.traklibrary.authentication.service.client.EmailClient;
import com.traklibrary.authentication.service.event.OnRecoveryNeededEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecoveryNeededEventListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private RecoveryNeededEventListener recoveryNeededEventListener;

    @Test
    void onApplicationEvent_withNullRecoveryToken_doesntInvokeEmailClient() {
        // Arrange
        OnRecoveryNeededEvent onRecoveryNeededEvent = new OnRecoveryNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn(null);

        // Act
        recoveryNeededEventListener.onApplicationEvent(onRecoveryNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void onApplicationEvent_withEmptyRecoveryToken_doesntInvokeEmailClient() {
        // Arrange
        OnRecoveryNeededEvent onRecoveryNeededEvent = new OnRecoveryNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn("");

        // Act
        recoveryNeededEventListener.onApplicationEvent(onRecoveryNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void onApplicationEvent_withValidRecoveryToken_invokesEmailClient() {
        // Arrange
        OnRecoveryNeededEvent onRecoveryNeededEvent = new OnRecoveryNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn("recovery-token");

        // Act
        recoveryNeededEventListener.onApplicationEvent(onRecoveryNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.atMostOnce())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}
