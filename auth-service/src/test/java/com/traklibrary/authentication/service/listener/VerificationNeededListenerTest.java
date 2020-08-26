package com.traklibrary.authentication.service.listener;

import com.traklibrary.authentication.service.UserService;
import com.traklibrary.authentication.service.client.EmailClient;
import com.traklibrary.authentication.service.event.OnVerificationNeededEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerificationNeededListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private VerificationNeededListener verificationNeededListener;

    @Test
    void onApplicationEvent_withNullVerificationCode_doesntInvokeEmailClient() {
        // Arrange
        OnVerificationNeededEvent onVerificationNeededEvent = new OnVerificationNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn(null);

        // Act
        verificationNeededListener.onApplicationEvent(onVerificationNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void onApplicationEvent_withEmptyVerificationCode_doesntInvokeEmailClient() {
        // Arrange
        OnVerificationNeededEvent onVerificationNeededEvent = new OnVerificationNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn("");

        // Act
        verificationNeededListener.onApplicationEvent(onVerificationNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void onApplicationEvent_withValidVerificationCode_invokesEmailClient() {
        // Arrange
        OnVerificationNeededEvent onVerificationNeededEvent = new OnVerificationNeededEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createVerificationCode(ArgumentMatchers.anyString()))
                .thenReturn("recovery-token");

        // Act
        verificationNeededListener.onApplicationEvent(onVerificationNeededEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.atMostOnce())
                .sendRecoveryEmail(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}
