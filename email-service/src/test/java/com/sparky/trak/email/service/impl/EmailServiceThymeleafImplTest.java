package com.sparky.trak.email.service.impl;

import com.sparky.trak.email.service.exception.EmailFailedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailServiceThymeleafImplTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ITemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceThymeleafImpl emailService;

    @BeforeAll
    public void beforeAll() {
        MockitoAnnotations.initMocks(this);
        emailService.setFromAddress("from.address@test.com");
    }

    @Test
    public void sendVerificationEmail_withFailingToSend_throwsEmailFailedException() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.anyString(), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(EmailFailedException.class, () -> emailService.sendVerificationEmail("", (short)0));
    }

    @Test
    public void sendVerificationEmail_withNoIssues_sendsEmail() {
        // Arrange
        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(Mockito.mock(MimeMessage.class));

        Mockito.when(templateEngine.process(ArgumentMatchers.anyString(), ArgumentMatchers.any(IContext.class)))
                .thenReturn("");

        Mockito.doNothing()
                .when(javaMailSender).send(ArgumentMatchers.any(MimeMessage.class));

        // Act
        emailService.sendVerificationEmail("email.address@test.com", (short)1234);

        // Assert
        Mockito.verify(javaMailSender, Mockito.atMostOnce())
                .send(ArgumentMatchers.any(MimeMessage.class));
    }
}
