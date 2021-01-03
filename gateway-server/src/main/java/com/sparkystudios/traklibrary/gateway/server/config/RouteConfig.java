package com.sparkystudios.traklibrary.gateway.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String SEGMENT_REPLACE = "/${segment}";

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("trak-auth-server", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader(COOKIE)
                                .removeRequestHeader(SET_COOKIE)
                                .rewritePath("/auth(?<segment>/?.*)", SEGMENT_REPLACE))
                        .uri("lb://trak-auth-server"))
                .route("trak-email-server", r -> r
                        .path("/emails/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader(COOKIE)
                                .removeRequestHeader(SET_COOKIE)
                                .rewritePath("/emails(?<segment>/?.*)", SEGMENT_REPLACE))
                        .uri("lb://trak-email-server"))
                .route("trak-game-server", r -> r
                        .path("/games/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader(COOKIE)
                                .removeRequestHeader(SET_COOKIE)
                                .rewritePath("/games(?<segment>/?.*)", SEGMENT_REPLACE))
                        .uri("lb://trak-game-server"))
                .route("trak-image-server", r -> r
                        .path("/games/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader(COOKIE)
                                .removeRequestHeader(SET_COOKIE)
                                .rewritePath("/images(?<segment>/?.*)", SEGMENT_REPLACE))
                        .uri("lb://trak-image-server"))
                .route("trak-notification-server", r -> r
                        .path("/notifications/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader(COOKIE)
                                .removeRequestHeader(SET_COOKIE)
                                .rewritePath("/notifications(?<segment>/?.*)", SEGMENT_REPLACE))
                        .uri("lb://trak-notification-server"))
                .build();
    }
}
