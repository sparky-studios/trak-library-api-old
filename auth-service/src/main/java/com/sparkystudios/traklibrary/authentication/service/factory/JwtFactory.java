package com.sparkystudios.traklibrary.authentication.service.factory;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtFactory {

    @Value("${trak.security.jwt.secret-key}")
    private String secretKey;

    @Value("${trak.security.jwt.expiry-time}")
    private long expiryTime;

    @Value("${trak.security.jwt.refresh-expiry-time}")
    private long refreshExpiryTime;

    public String createAccessToken(UserDto userDto) {
        long now = System.currentTimeMillis();

        // Ensure the account has authorities, otherwise they won't be able to access anything in the system.
        if (userDto.getAuthorities() == null || userDto.getAuthorities().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an access token for a user with no roles.");
        }

        // Create the claims for the token.
        Claims claims = Jwts.claims();
        claims.put("scopes", userDto.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        claims.put("userId", userDto.getId());
        claims.put("verified", userDto.isVerified());

        // Create the token and populate some information about it.
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(userDto.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiryTime))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }

    public String createRefreshToken(UserDto userDto) {
        long now = System.currentTimeMillis();

        // Create the claims for the token.
        Claims claims = Jwts.claims();
        claims.put("scopes", "TOKEN_REFRESH");
        claims.put("userId", userDto.getId());
        claims.put("verified", userDto.isVerified());

        // Create the token and populate some information about it.
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer("Trak Library")
                .setSubject(userDto.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshExpiryTime))
                .signWith(SignatureAlgorithm.HS512, secretKey.getBytes())
                .compact();
    }
}
