package com.sparkystudios.traklibrary.gateway.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("trak-auth-server", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Set-Cookie")
                                .rewritePath("/auth(?<segment>/?.*)", "/${segment}"))
                        .uri("lb://trak-auth-server"))
                .route("trak-email-server", r -> r
                        .path("/emails/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Set-Cookie")
                                .rewritePath("/emails(?<segment>/?.*)", "/${segment}"))
                        .uri("lb://trak-email-server"))
                .route("trak-game-server", r -> r
                        .path("/games/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Set-Cookie")
                                .rewritePath("/games(?<segment>/?.*)", "/${segment}"))
                        .uri("lb://trak-game-server"))
                .route("trak-image-server", r -> r
                        .path("/games/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Set-Cookie")
                                .rewritePath("/images(?<segment>/?.*)", "/${segment}"))
                        .uri("lb://trak-image-server"))
                .route("trak-notification-server", r -> r
                        .path("/notifications/**")
                        .filters(f -> f
                                .tokenRelay()
                                .removeRequestHeader("Cookie")
                                .removeRequestHeader("Set-Cookie")
                                .rewritePath("/notifications(?<segment>/?.*)", "/${segment}"))
                        .uri("lb://trak-notification-server"))
                .build();
    }
}
