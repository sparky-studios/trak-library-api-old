package com.sparkystudios.traklibrary.email.service.impl;

import com.sparkystudios.traklibrary.email.service.dto.EmailPasswordChangedRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import com.sparkystudios.traklibrary.email.service.exception.EmailFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class EmailServiceThymeleafImplTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceThymeleafImpl emailService;

    @Test
    void sendVerificationEmail_withFailingToSend_throwsEmailFailedException() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("verification-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto();

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendVerificationEmail(emailVerificationRequestDto));
    }

    @Test
    void sendVerificationEmail_withNoIssues_sendsEmail() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("verification-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        Mockito.doNothing()
                .when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));

        EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto();
        emailVerificationRequestDto.setEmailAddress("test@traklibrary.com");
        emailVerificationRequestDto.setVerificationCode("12345");

        // Act
        emailService.sendVerificationEmail(emailVerificationRequestDto);

        // Assert
        Mockito.verify(javaMailSender, Mockito.atMostOnce())
                .send(ArgumentMatchers.any(MimeMessage.class));
    }

    @Test
    void sendRecoveryEmail_withFailingToSend_throwsEmailFailedException() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("recovery-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        EmailRecoveryRequestDto emailRecoveryRequestDto = new EmailRecoveryRequestDto();

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendRecoveryEmail(emailRecoveryRequestDto));
    }

    @Test
    void sendRecoveryEmail_withNoIssues_sendsEmail() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("recovery-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        Mockito.doNothing()
                .when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));

        EmailRecoveryRequestDto emailRecoveryRequestDto = new EmailRecoveryRequestDto();
        emailRecoveryRequestDto.setEmailAddress("test@traklibrary.com");
        emailRecoveryRequestDto.setRecoveryToken("12345");

        // Act
        emailService.sendRecoveryEmail(emailRecoveryRequestDto);

        // Assert
        Mockito.verify(javaMailSender, Mockito.atMostOnce())
                .send(ArgumentMatchers.any(MimeMessage.class));
    }

    @Test
    void sendPasswordChangedEmail_withFailingToSend_throwsEmailFailedException() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("password-changed-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        EmailPasswordChangedRequestDto emailPasswordChangedRequestDto = new EmailPasswordChangedRequestDto();

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendPasswordChangedEmail(emailPasswordChangedRequestDto));
    }

    @Test
    void sendPasswordChangedEmail_withNoIssues_sendsEmail() {
        // Arrange
        emailService.setFromAddress("from@traklibrary.com");

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.eq("password-changed-template"), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        Mockito.doNothing()
                .when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));

        EmailPasswordChangedRequestDto emailPasswordChangedRequestDto = new EmailPasswordChangedRequestDto();
        emailPasswordChangedRequestDto.setEmailAddress("test@traklibrary.com");

        // Act
        emailService.sendPasswordChangedEmail(emailPasswordChangedRequestDto);

        // Assert
        Mockito.verify(javaMailSender, Mockito.atMostOnce())
                .send(ArgumentMatchers.any(MimeMessage.class));
    }
}
