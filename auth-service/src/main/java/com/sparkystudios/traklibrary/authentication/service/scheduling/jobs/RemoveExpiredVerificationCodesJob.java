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
 * Quarts job that is executed once an hour, on the hour. Its purpose is to remove any verification codes
 * that have expired for users that have not yet completed the verification process. The expiration time for a
 * verification code is around 24 hours, depending on when the verification code requested. Once a code has expired,
 * the user will have to request a new one.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveExpiredVerificationCodesJob implements Job {

    private final UserRepository userRepository;

    /**
     * Quarts job that is executed once an hour, on the hour. Its purpose is to remove any verification codes
     * that have expired for users that have not yet completed the verification process. The expiration time for a
     * verification code is around 24 hours, depending on when the verification code requested. Once a code has expired,
     * the user will have to request a new one.
     *
     * @param jobExecutionContext The {@link JobExecutionContext} that executed the job.
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // Get the name of the current thread that this scheduled task is running on, for debug purposes.
        String currentThread = Thread.currentThread().getName();
        log.info(String.format("---------- Running scheduled task: Remove expired verification codes on thread: %s  ----------", currentThread));

        // Retrieve all of the accounts that aren't verified and verification code is older than 24 hours.
        Collection<User> users = userRepository
                .findByVerifiedIsFalseAndVerificationExpiryDateBefore(LocalDateTime.now());

        // Loop through each user that matches the criteria and remove both the expiry date and verification code.
        // A new verification code won't be automatically generated, the user will have to request a new one.
        users.forEach(user -> {
            user.setVerificationExpiryDate(null);
            user.setVerificationCode(null);

            // Save the updated entity, but leave them as not verified.
            userRepository.save(user);
        });

        log.info(String.format("---------- Finished scheduled task: Remove expired verification codes on thread: %s ----------", currentThread));
    }
}
