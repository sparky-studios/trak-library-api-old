package com.sparkystudios.traklibrary.authentication.service.client.impl;

import com.sparkystudios.traklibrary.authentication.service.client.EmailClient;
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
    public void sendVerificationEmail(String emailAddress, String verificationCode) {
        String url = "http://trak-email-server/verification?email-address={emailAddress}&verification-code={verificationCode}";

        sendVerificationEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(getHeaders()), Void.class, emailAddress, verificationCode), throwable -> {
            log.error("failed to send verification email", throwable);
            return null;
        });
    }

    @Override
    public void sendRecoveryEmail(String emailAddress, String recoveryToken) {
        String url = "http://trak-email-server/recovery?email-address={emailAddress}&recovery-token={recoveryToken}";

        sendRecoveryEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT,  new HttpEntity<>(getHeaders()), Void.class, emailAddress, recoveryToken), throwable -> {
            log.error("failed to send account recovery email", throwable);
            return null;
        });
    }

    @Override
    public void sendChangePasswordEmail(String emailAddress, String recoveryToken) {
        String url = "http://trak-email-server/change-password?email-address={emailAddress}&recovery-token={recoveryToken}";

        sendChangePasswordEmailCircuitBreaker.run(() -> restTemplate.exchange(url, HttpMethod.PUT,  new HttpEntity<>(getHeaders()), Void.class, emailAddress, recoveryToken), throwable -> {
            log.error("failed to send change password email", throwable);
            return null;
        });
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.set(HttpHeaders.ACCEPT, "application/vnd.traklibrary.v1+json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
