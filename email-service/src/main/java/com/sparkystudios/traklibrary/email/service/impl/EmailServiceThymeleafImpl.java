package com.sparkystudios.traklibrary.email.service.impl;

import com.sparkystudios.traklibrary.email.service.EmailService;
import com.sparkystudios.traklibrary.email.service.dto.EmailDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.email.service.dto.EmailVerificationRequestDto;
import com.sparkystudios.traklibrary.email.service.exception.EmailFailedException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class EmailServiceThymeleafImpl implements EmailService {

    private static final String VERIFICATION_SUBJECT = "email.verification.subject";
    private static final String RECOVERY_SUBJECT = "email.recovery.subject";
    private static final String CHANGE_PASSWORD_SUBJECT = "email.change-password.subject";

    @Setter
    @Value("${trak.aws.ses.from-address}")
    private String fromAddress;

    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Async
    @Override
    public void sendVerificationEmail(EmailVerificationRequestDto emailVerificationRequestDto) {
        // Create the Email template and all the data it needs before sending.
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom(fromAddress);
        emailDto.setTo(emailVerificationRequestDto.getEmailAddress());
        emailDto.setSubject(messageSource.getMessage(VERIFICATION_SUBJECT, new Object[] {}, LocaleContextHolder.getLocale()));
        emailDto.setData(Collections.singletonMap("verificationCode", emailVerificationRequestDto.getVerificationCode()));

        try {
            javaMailSender.send(getMimeMessage(emailDto, "verification-template"));
        } catch (Exception e) {
            throw new EmailFailedException("Failed to send verification email.", e);
        }
    }

    @Async
    @Override
    public void sendRecoveryEmail(EmailRecoveryRequestDto emailRecoveryRequestDto) {
        // Create the Email template and all the data it needs before sending.
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom(fromAddress);
        emailDto.setTo(emailRecoveryRequestDto.getEmailAddress());
        emailDto.setSubject(messageSource.getMessage(RECOVERY_SUBJECT, new Object[] {}, LocaleContextHolder.getLocale()));
        emailDto.setData(Collections.singletonMap("recoveryToken", emailRecoveryRequestDto.getRecoveryToken()));

        try {
            javaMailSender.send(getMimeMessage(emailDto, "recovery-template"));
        } catch (Exception e) {
            throw new EmailFailedException("Failed to send recovery email.", e);
        }
    }

    @Async
    @Override
    public void sendChangePasswordEmail(EmailRecoveryRequestDto emailRecoveryRequestDto) {
        // Create the Email template and all the data it needs before sending.
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom(fromAddress);
        emailDto.setTo(emailRecoveryRequestDto.getEmailAddress());
        emailDto.setSubject(messageSource.getMessage(CHANGE_PASSWORD_SUBJECT, new Object[] {}, LocaleContextHolder.getLocale()));
        emailDto.setData(Collections.singletonMap("recoveryToken", emailRecoveryRequestDto.getRecoveryToken()));

        try {
            javaMailSender.send(getMimeMessage(emailDto, "change-password-template"));
        } catch (Exception e) {
            throw new EmailFailedException("Failed to send change password email.", e);
        }
    }

    private MimeMessage getMimeMessage(EmailDto emailDto, String template) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(emailDto.getData());

        String html = templateEngine.process(template, context);

        mimeMessageHelper.setTo(emailDto.getTo());
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setSubject(emailDto.getSubject());
        mimeMessageHelper.setFrom(new InternetAddress(emailDto.getFrom(), "Trak Library"));

        return mimeMessage;
    }
}
