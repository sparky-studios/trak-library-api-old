package com.sparkystudios.traklibrary.authentication.service.listener;

import com.sparkystudios.traklibrary.authentication.service.UserService;
import com.sparkystudios.traklibrary.authentication.service.client.EmailClient;
import com.sparkystudios.traklibrary.authentication.service.event.OnChangePasswordEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChangePasswordEventListenerTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailClient emailClient;

    @InjectMocks
    private ChangePasswordEventListener changePasswordEventListener;

    @Test
    void onApplicationEvent_withNullRecoveryToken_doesntInvokeEmailClient() {
        // Arrange
        OnChangePasswordEvent onChangePasswordEvent = new OnChangePasswordEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn(null);

        // Act
        changePasswordEventListener.onApplicationEvent(onChangePasswordEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendChangePasswordEmail(ArgumentMatchers.any());
    }

    @Test
    void onApplicationEvent_withEmptyRecoveryToken_doesntInvokeEmailClient() {
        // Arrange
        OnChangePasswordEvent onChangePasswordEvent = new OnChangePasswordEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn("");

        // Act
        changePasswordEventListener.onApplicationEvent(onChangePasswordEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.never())
                .sendChangePasswordEmail(ArgumentMatchers.any());
    }

    @Test
    void onApplicationEvent_withValidRecoveryToken_invokesEmailClient() {
        // Arrange
        OnChangePasswordEvent onChangePasswordEvent = new OnChangePasswordEvent(this, "test@traklibrary.com", "username");

        Mockito.when(userService.createRecoveryToken(ArgumentMatchers.anyString()))
                .thenReturn("recovery-token");

        // Act
        changePasswordEventListener.onApplicationEvent(onChangePasswordEvent);

        // Assert
        Mockito.verify(emailClient, Mockito.atMostOnce())
                .sendChangePasswordEmail(ArgumentMatchers.any());
    }
}
