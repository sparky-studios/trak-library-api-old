package com.sparkystudios.traklibrary.authentication.server.configuration;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Enables the Eureka client functionality only if the spring profiles are either development or production.
 * This is done so that during the integration testing phase, the game-server isn't registered as a eureka client
 * as it can cause un-needed exceptions and registering within a discovery server isn't necessary for endpoint testing.
 *
 * @author Sparky Studios
 */
@Profile({ "development", "staging", "production" })
@Configuration
@EnableEurekaClient
public class EurekaClientConfiguration {

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
