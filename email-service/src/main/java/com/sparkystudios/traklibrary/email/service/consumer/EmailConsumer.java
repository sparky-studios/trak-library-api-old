package com.sparkystudios.traklibrary.email.service.consumer;

import com.sparkystudios.traklibrary.email.service.EmailService;
import com.sparkystudios.traklibrary.email.service.dto.EmailPasswordChangedRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import com.sparkystudios.traklibrary.email.service.event.PasswordChangedEvent;
import com.sparkystudios.traklibrary.email.service.event.RecoveryEvent;
import com.sparkystudios.traklibrary.email.service.event.VerificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailConsumer {

    private final EmailService emailService;

    /**
     * {@link Consumer} registered with Spring Cloud Stream that responds to any published
     * "trak-email-verification" events. Its' purpose is to merely convert from the
     * {@link VerificationEvent} sent from the publisher, to a {@link EmailVerificationRequestDto}
     * so it can be send via the email provider in the {@link EmailService}.
     *
     * @return The {@link Consumer} that consumes the Spring Cloud Stream event.
     */
    @Bean
    public Consumer<VerificationEvent> sendVerificationEmail() {
        return verificationEvent -> {
            log.info("Running verification event for: " + verificationEvent.getUsername());

            var emailVerificationRequestDto = new EmailVerificationRequestDto();
            emailVerificationRequestDto.setEmailAddress(verificationEvent.getEmailAddress());
            emailVerificationRequestDto.setVerificationCode(verificationEvent.getVerificationCode());

            emailService.sendVerificationEmail(emailVerificationRequestDto);
        };
    }

    /**
     * {@link Consumer} registered with Spring Cloud Stream that responds to any published
     * "trak-email-recovery" events. Its' purpose is to merely convert from the
     * {@link RecoveryEvent} sent from the publisher, to a {@link EmailRecoveryRequestDto}
     * so it can be send via the email provider in the {@link EmailService}.
     *
     * @return The {@link Consumer} that consumes the Spring Cloud Stream event.
     */
    @Bean
    public Consumer<RecoveryEvent> sendRecoveryEmail() {
        return recoveryEvent -> {
            log.info("Running recovery event for: " + recoveryEvent.getUsername());

            var emailRecoveryRequestDto = new EmailRecoveryRequestDto();
            emailRecoveryRequestDto.setEmailAddress(recoveryEvent.getEmailAddress());
            emailRecoveryRequestDto.setRecoveryToken(recoveryEvent.getRecoveryToken());

            emailService.sendRecoveryEmail(emailRecoveryRequestDto);
        };
    }

    /**
     * {@link Consumer} registered with Spring Cloud Stream that responds to any published
     * "trak-email-password-changed" events. Its' purpose is to merely convert from the
     * {@link PasswordChangedEvent} sent from the publisher, to a {@link EmailRecoveryRequestDto}
     * so it can be send via the email provider in the {@link EmailService}.
     *
     * @return The {@link Consumer} that consumes the Spring Cloud Stream event.
     */
    @Bean
    public Consumer<PasswordChangedEvent> sendPasswordChangedEmail() {
        return passwordChangedEvent -> {
            log.info("Running password changed event for: " + passwordChangedEvent.getUsername());

            var emailRecoveryRequestDto = new EmailPasswordChangedRequestDto();
            emailRecoveryRequestDto.setEmailAddress(passwordChangedEvent.getEmailAddress());

            emailService.sendPasswordChangedEmail(emailRecoveryRequestDto);
        };
    }
}
