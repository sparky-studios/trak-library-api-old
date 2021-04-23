package com.sparkystudios.traklibrary.authentication.service.client.impl;

import com.sparkystudios.traklibrary.authentication.service.client.EmailClient;
import com.sparkystudios.traklibrary.authentication.service.dto.EmailRecoveryRequestDto;
import com.sparkystudios.traklibrary.authentication.service.dto.EmailVerificationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailClientCircuitBreakerImpl implements EmailClient {

    @Value("${trak.security.user.email.username}")
    private String username;

    @Value("${trak.security.user.email.password}")
    private String password;

    private final RestTemplate restTemplate;
    @SuppressWarnings("all")
    private final CircuitBreakerFactory circuitBreakerFactory;

    private CircuitBreaker sendVerificationEmailCircuitBreaker;
    private CircuitBreaker sendRecoveryEmailCircuitBreaker;
    private CircuitBreaker sendChangePasswordEmailCircuitBreaker;

    @PostConstruct
    private void postConstruct() {
        sendVerificationEmailCircuitBreaker = circuitBreakerFactory.create("send-verification-email");
        sendRecoveryEmailCircuitBreaker = circuitBreakerFactory.create("send-recovery-email");
        sendChangePasswordEmailCircuitBreaker = circuitBreakerFactory.create("send-change-password-email");
    }

    @Override
    public void sendVerificationEmail(EmailVerificationRequestDto emailVerificationRequestDto) {
        var url = "http://trak-email-server/verification";

        sendVerificationEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(emailVerificationRequestDto, getHeaders()), Void.class), throwable -> {
            log.error("failed to send verification email", throwable);
            return null;
        });
    }

    @Override
    public void sendRecoveryEmail(EmailRecoveryRequestDto emailRecoveryRequestDto) {
        var url = "http://trak-email-server/recovery";

        sendRecoveryEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT,  new HttpEntity<>(emailRecoveryRequestDto, getHeaders()), Void.class), throwable -> {
            log.error("failed to send account recovery email", throwable);
            return null;
        });
    }

    @Override
    public void sendChangePasswordEmail(EmailRecoveryRequestDto emailRecoveryRequestDto) {
        var url = "http://trak-email-server/change-password";

        sendChangePasswordEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT,  new HttpEntity<>(emailRecoveryRequestDto, getHeaders()), Void.class), throwable -> {
            log.error("failed to send change password email", throwable);
            return null;
        });
    }

    private HttpHeaders getHeaders() {
        var headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.set(HttpHeaders.ACCEPT, "application/vnd.traklibrary.v1+json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
