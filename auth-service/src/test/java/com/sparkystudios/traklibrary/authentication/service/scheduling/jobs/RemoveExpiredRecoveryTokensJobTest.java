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
class RemoveExpiredRecoveryTokensJobTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RemoveExpiredRecoveryTokensJob removeExpiredRecoveryTokensJob;

    @Test
    void execute_withNoUsers_doesntMakeChanges() {
        // Arrange
        Mockito.when(userRepository.findByRecoveryTokenExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.emptyList());

        // Act
        removeExpiredRecoveryTokensJob.execute(Mockito.mock(JobExecutionContext.class));

        // Assert
        Mockito.verify(userRepository, Mockito.never())
                .save(ArgumentMatchers.any());
    }

    @Test
    void execute_withUsers_removesRecoveryTokenAndExpiryDateAndSaves() {
        // Arrange
        User user = Mockito.spy(User.class);

        Mockito.when(userRepository.findByRecoveryTokenExpiryDateBefore(ArgumentMatchers.any()))
                .thenReturn(Collections.singletonList(user));

        // Act
        removeExpiredRecoveryTokensJob.execute(Mockito.mock(JobExecutionContext.class));

        // Assert
        Mockito.verify(user, Mockito.atMostOnce())
                .setRecoveryTokenExpiryDate(null);

        Mockito.verify(user, Mockito.atMostOnce())
                .setRecoveryToken(null);

        Mockito.verify(userRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }
}
