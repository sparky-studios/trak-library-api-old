package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.service.TokenService;
import com.sparkystudios.traklibrary.security.context.UserContext;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class TokenServiceJwtImpl implements TokenService {

    @Value("${trak.security.jwt.secret-key}")
    private String secretKey;

    @Value("${trak.security.jwt.expiry-time}")
    private long expiryTime;

    @Value("${trak.security.jwt.refresh-expiry-time}")
    private long refreshExpiryTime;

    @Override
    public String createAccessToken(UserContext userContext) {
        long now = System.currentTimeMillis();

        // Ensure the account has authorities, otherwise they won't be able to access anything in the system.
        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an access token for a user with no roles.");
        }

        // Create the token and populate some information about it.
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(userContext.getUsername())
                .claim("scopes", userContext.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .claim("userId", userContext.getUserId())
                .claim("verified", userContext.isVerified())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiryTime))
                .setAudience("https://api.traklibrary.com")
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    @Override
    public String createRefreshToken(UserContext userContext) {
        long now = System.currentTimeMillis();

        // Create the token and populate some information about it.
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(userContext.getUsername())
                .claim("scopes", List.of("TOKEN_REFRESH"))
                .claim("userId", userContext.getUserId())
                .claim("verified", userContext.isVerified())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpiryTime))
                .setAudience("https://api.traklibrary.com")
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }
}
