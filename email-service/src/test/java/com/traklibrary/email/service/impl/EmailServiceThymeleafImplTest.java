package com.traklibrary.email.service.impl;

import com.traklibrary.email.service.exception.EmailFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendVerificationEmail("", ""));
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

        // Act
        emailService.sendVerificationEmail("email.address@test.com", "12345");

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

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendRecoveryEmail("", ""));
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

        // Act
        emailService.sendRecoveryEmail("email.address@test.com", "12345");

        // Assert
        Mockito.verify(javaMailSender, Mockito.atMostOnce())
                .send(ArgumentMatchers.any(MimeMessage.class));
    }
}
