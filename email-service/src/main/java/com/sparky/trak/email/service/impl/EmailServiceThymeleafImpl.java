package com.sparky.trak.email.service.impl;

import com.sparky.trak.email.service.EmailService;
import com.sparky.trak.email.service.dto.EmailDto;
import com.sparky.trak.email.service.exception.EmailFailedException;
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
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class EmailServiceThymeleafImpl implements EmailService {

    @Setter
    @Value("${trak.aws.simple-email-service.from-address}")
    private String fromAddress;

    private final JavaMailSender javaMailSender;
    private final ITemplateEngine templateEngine;
    private final MessageSource messageSource;

    @Async
    @Override
    public void sendVerificationEmail(String emailAddress, String verificationCode) {
        // Create the Email template and all the data it needs before sending.
        EmailDto emailDto = new EmailDto();
        emailDto.setFrom(fromAddress);
        emailDto.setTo(emailAddress);
        emailDto.setSubject(messageSource.getMessage("email.verification.subject", new Object[] {}, LocaleContextHolder.getLocale()));
        emailDto.setData(Collections.singletonMap("verificationCode", verificationCode));

        try {
            javaMailSender.send(getMimeMessage(emailDto, "verification-template"));
        } catch (Exception e) {
            throw new EmailFailedException("Failed to send verification email.", e);
        }
    }

    private MimeMessage getMimeMessage(EmailDto emailDto, String template) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(emailDto.getData());

        String html = templateEngine.process(template, context);

        mimeMessageHelper.setTo(emailDto.getTo());
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setSubject(emailDto.getSubject());
        mimeMessageHelper.setFrom(emailDto.getFrom());

        return mimeMessage;
    }
}
