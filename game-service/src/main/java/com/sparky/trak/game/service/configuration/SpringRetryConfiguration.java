package com.sparky.trak.game.service.configuration;

import org.springframework.cloud.circuitbreaker.springretry.SpringRetryCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.springretry.SpringRetryConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.TimeoutRetryPolicy;

@Configuration
public class SpringRetryConfiguration {

    @Bean
    public Customizer<SpringRetryCircuitBreakerFactory> springRetryCircuitBreakerFactoryCustomizer() {
        return factory -> factory.configureDefault(id -> {
            TimeoutRetryPolicy timeoutRetryPolicy = new TimeoutRetryPolicy();
            timeoutRetryPolicy.setTimeout(4000L);

            return new SpringRetryConfigBuilder(id)
                    .retryPolicy(timeoutRetryPolicy)
                    .build();
        });
    }
}
