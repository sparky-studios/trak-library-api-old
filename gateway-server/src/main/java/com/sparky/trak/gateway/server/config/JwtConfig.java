package com.sparky.trak.gateway.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfig {

    @Value("${security.jwt.auth-uri}")
    private String authUri;

    @Value("${security.jwt.secret-key}")
    private String secretKey;
}

