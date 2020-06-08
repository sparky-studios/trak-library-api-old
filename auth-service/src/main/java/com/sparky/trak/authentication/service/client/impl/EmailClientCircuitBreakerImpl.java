package com.sparky.trak.authentication.service.client.impl;

import com.sparky.trak.authentication.service.client.EmailClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailClientCircuitBreakerImpl implements EmailClient {

    private final RestTemplate restTemplate;
    @SuppressWarnings("rawtypes")
    private final CircuitBreakerFactory circuitBreakerFactory;

    private CircuitBreaker sendVerificationEmailCircuitBreaker;
    private CircuitBreaker sendRecoveryEmailCircuitBreaker;

    @PostConstruct
    private void postConstruct() {
        sendVerificationEmailCircuitBreaker = circuitBreakerFactory.create("send-verification-email");
        sendRecoveryEmailCircuitBreaker = circuitBreakerFactory.create("send-recovery-email");
    }

    @Override
    public void sendVerificationEmail(String emailAddress, String verificationCode) {
        String url = "http://trak-email-server/v1/emails/verification?email-address={emailAddress}&verification-code={verificationCode}";
        sendVerificationEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT, null, Void.class, emailAddress, verificationCode), throwable -> {
            log.error("failed to send verification email", throwable);
            return null;
        });
    }

    @Override
    public void sendRecoveryEmail(String emailAddress, String recoveryToken) {
        String url = "http://trak-email-server/v1/emails/recovery?email-address={emailAddress}&recovery-token={recoveryToken}";
        sendRecoveryEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT, null, Void.class, emailAddress, recoveryToken), throwable -> {
            log.error("failed to send account recovery email", throwable);
            return null;
        });
    }
}
