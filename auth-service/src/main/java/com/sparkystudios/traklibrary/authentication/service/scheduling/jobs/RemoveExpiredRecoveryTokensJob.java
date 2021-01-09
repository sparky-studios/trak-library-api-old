package com.sparkystudios.traklibrary.authentication.service.scheduling.jobs;

import com.sparkystudios.traklibrary.authentication.domain.User;
import com.sparkystudios.traklibrary.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Quartz job that is executed once an hour, at the half-past mark. Its purpose is to remove any recovery tokens
 * that have expired for users that have not yet completed account recovery. The expiration time for a recovery token is
 * around 24 hours, depending on when the recovery token was requested. Once a token has expired, the user will have to
 * request a new one.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveExpiredRecoveryTokensJob implements Job {

    private final UserRepository userRepository;

    /**
     * Quartz job that is executed once an hour, at the half-past mark. Its purpose is to remove any recovery tokens
     * that have expired for users that have not yet completed account recovery. The expiration time for a recovery token is
     * around 24 hours, depending on when the recovery token was requested. Once a token has expired, the user will have to
     * request a new one.
     *
     * @param jobExecutionContext The {@link JobExecutionContext} that executed the job.
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // Get the name of the current thread that this scheduled task is running on, for debug purposes.
        String currentThread = Thread.currentThread().getName();
        log.info(String.format("---------- Running scheduled task: Remove expired recovery tokens on thread: %s ----------", currentThread));

        // Retrieve all of the accounts that have recovery tokens whose expiry date have eclipsed 24 hours..
        Collection<User> users = userRepository
                .findByRecoveryTokenExpiryDateBefore(LocalDateTime.now().minusDays(1));

        // Loop through each user that matches the criteria and remove both the expiry date and the recovery token.
        // A new recovery token won't be automatically generated, the user will have to request a new one.
        users.forEach(user -> {
            user.setRecoveryTokenExpiryDate(null);
            user.setRecoveryToken(null);

            userRepository.save(user);
        });

        log.info(String.format("---------- Finished scheduled task: Remove expired recovery tokens on thread: %s ----------", currentThread));
    }
}
