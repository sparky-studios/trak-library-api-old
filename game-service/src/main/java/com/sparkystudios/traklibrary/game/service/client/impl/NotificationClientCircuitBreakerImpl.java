package com.sparkystudios.traklibrary.game.service.client.impl;

import com.sparkystudios.traklibrary.game.service.client.NotificationClient;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationClientCircuitBreakerImpl implements NotificationClient {

    private final AuthenticationService authenticationService;
    @SuppressWarnings("all")
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RestTemplate restTemplate;

    @Setter
    private CircuitBreaker notificationServerCircuitBreaker;

    @PostConstruct
    private void postConstruct() {
        notificationServerCircuitBreaker = circuitBreakerFactory.create("notification-server-circuit-breaker");
    }

    @Override
    public void send(long userId, String title, String content) {
        // Ensure we specify the authentication in the headers.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(authenticationService.getToken());

        // We don't care about the return type, just the headers.
        HttpEntity<Object> requestEntity
                = new HttpEntity<>(null, httpHeaders);

        // Encode the title and message to UTF-8 to prevent any issues with string being passed as request parameters.
        String encodedTitle = StringUtils.newStringUtf8(title.getBytes());
        String encodedContent = StringUtils.newStringUtf8(content.getBytes());

        // Send the request using the circuit breaker pattern.
        notificationServerCircuitBreaker.run(() ->
                restTemplate.postForEntity("http://trak-notification-server/send?user-id={userId}&title={title}&content={content}",
                        requestEntity, Void.class, userId, encodedTitle, encodedContent), throwable -> {
            // No point failing the whole request, just log the failure.
            log.error("Failed to send push notification to: " + userId, throwable);
            return null;
        });
    }
}
