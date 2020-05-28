package com.sparky.trak.authentication.service.scheduler;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class AuthTaskSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthTaskScheduler authTaskScheduler;

    @Test
    public void removeExpiredVerificationCodesScheduledTask_withNoUsers_doesntMakeChanges() {
        // Arrange
        Mockito.when(userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        authTaskScheduler.removeExpiredVerificationCodesScheduledTask();

        // Assert
        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void removeExpiredVerificationCodesScheduledTask_withUsers_removesVerificationCodeAndExpiryDateAndSaves() {
        // Arrange
        User user = Mockito.spy(User.class);

        Mockito.when(userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(user));

        // Act
        authTaskScheduler.removeExpiredVerificationCodesScheduledTask();

        // Assert
        Mockito.verify(user, Mockito.atMostOnce())
                .setVerificationExpiryDate(null);

        Mockito.verify(user, Mockito.atMostOnce())
                .setVerificationCode(null);

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }
}
