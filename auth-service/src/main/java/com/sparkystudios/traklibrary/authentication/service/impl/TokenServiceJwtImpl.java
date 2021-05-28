package com.sparkystudios.traklibrary.authentication.service.impl;

import com.sparkystudios.traklibrary.authentication.service.TokenService;
import com.sparkystudios.traklibrary.security.KeyService;
import com.sparkystudios.traklibrary.security.context.UserContext;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class TokenServiceJwtImpl implements TokenService {

    @Value("${trak.security.jwt.expiry-time}")
    private long expiryTime;

    @Value("${trak.security.jwt.refresh-expiry-time}")
    private long refreshExpiryTime;

    @Value("${trak.security.jwt.private-key}")
    private String privateKeyText;

    private final KeyService keyService;

    private Key privateKey;

    @PostConstruct
    public void postConstruct() {
        privateKey = keyService.readPrivateKey(privateKeyText);
    }

    @Override
    public String createAccessToken(UserContext userContext, String role, Iterable<String> scopes) {
        // Ensure the account has authorities, otherwise they won't be able to access anything in the system.
        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an access token for a user with no authorities.");
        }

        // Create the token and populate some information about it.
        return createToken(userContext.getUsername(), role,
                scopes, userContext.getUserId(), userContext.isVerified());
    }

    @Override
    public String createRefreshToken(UserContext userContext) {
        // Create the token and populate some information about it.
        return createToken(userContext.getUsername(), "ROLE_TOKEN_REFRESH",
                Collections.emptyList(), userContext.getUserId(), userContext.isVerified());
    }

    String createToken(String username, String role, Iterable<String> scopes, long userId, boolean verified) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(username)
                .claim("role", role)
                .claim("scope", scopes)
                .claim("userId", userId)
                .claim("verified", verified)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiryTime))
                .setAudience("https://api.traklibrary.com")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }
}
