package com.sparkystudios.traklibrary.authentication.service.scheduling.jobs;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class RemoveExpiredVerificationCodesJobTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RemoveExpiredVerificationCodesJob removeExpiredVerificationCodesJob;

    @Test
    void execute_withNoUsers_doesntMakeChanges() {
        // Arrange
        Mockito.when(userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        removeExpiredVerificationCodesJob.execute(Mockito.mock(JobExecutionContext.class));

        // Assert
        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void removeExpiredVerificationCodesScheduledTask_withUsers_removesVerificationCodeAndExpiryDateAndSaves() {
        // Arrange
        User user = Mockito.spy(User.class);

        Mockito.when(userRepository.findByVerifiedIsFalseAndVerificationExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(user));

        // Act
        removeExpiredVerificationCodesJob.execute(Mockito.mock(JobExecutionContext.class));

        // Assert
        Mockito.verify(user, Mockito.atMostOnce())
                .setVerificationExpiryDate(null);

        Mockito.verify(user, Mockito.atMostOnce())
                .setVerificationCode(null);

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }
}
