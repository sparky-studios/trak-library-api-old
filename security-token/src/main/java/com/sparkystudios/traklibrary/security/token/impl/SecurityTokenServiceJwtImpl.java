package com.sparkystudios.traklibrary.security.token.impl;

import com.sparkystudios.traklibrary.security.token.KeyService;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.adapter.JwtSecurityTokenAdapter;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class SecurityTokenServiceJwtImpl implements SecurityTokenService {

    @Value("${trak.security.jwt.expiry-time}")
    private long expiryTime;

    @Value("${trak.security.jwt.two-factor-auth-expiry-time}")
    private long twoFactorAuthExpiryTime;

    @Value("${trak.security.jwt.refresh-expiry-time}")
    private long refreshExpiryTime;

    @Value("${trak.security.jwt.private-key}")
    private String privateKeyText;

    @Value("${trak.security.jwt.public-key}")
    private String publicKeyText;

    private final KeyService keyService;

    private Key privateKey;
    private Key publicKey;

    @PostConstruct
    public void postConstruct() {
        privateKey = keyService.readPrivateKey(privateKeyText);
        publicKey = keyService.readPublicKey(publicKeyText);
    }

    @Override
    public SecurityToken getToken(String token) {
        var claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new JwtSecurityTokenAdapter(claims, token);
    }

    @Override
    public SecurityToken createAccessToken(UserData userData, UserSecurityRole userSecurityRole, Iterable<String> scopes) {
        // Ensure the account has authorities, otherwise they won't be able to access anything in the system.
        if (userData.getAuthorities() == null || userData.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an access token for a user with no authorities.");
        }

        // Create the token and populate some information about it.
        return createToken(userData.getUsername(), userSecurityRole,
                scopes, userData.getUserId(), userData.isVerified(), expiryTime);
    }

    @Override
    public SecurityToken createTwoFactorAuthenticationToken(UserData userData) {
        // Create the token and populate some information about it.
        return createToken(userData.getUsername(), UserSecurityRole.ROLE_TWO_FACTOR_AUTHENTICATION_TOKEN,
                Collections.emptyList(), userData.getUserId(), userData.isVerified(), twoFactorAuthExpiryTime);
    }

    @Override
    public SecurityToken createRefreshToken(UserData userData) {
        // Create the token and populate some information about it.
        return createToken(userData.getUsername(), UserSecurityRole.ROLE_TOKEN_REFRESH,
                Collections.emptyList(), userData.getUserId(), userData.isVerified(), refreshExpiryTime);
    }

    SecurityToken createToken(String username, UserSecurityRole userSecurityRole, Iterable<String> scopes, long userId, boolean verified, long expiry) {
        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(username)
                .claim("role", userSecurityRole.name())
                .claim("scope", scopes)
                .claim("userId", userId)
                .claim("verified", verified)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiry))
                .setAudience("https://api.traklibrary.com")
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

        return getToken(token);
    }
}