package com.sparkystudios.traklibrary.security.token.adapter;

import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The {@link JwtSecurityTokenAdapter} is an adapter class that adapts from the jsonwebtoken
 * library's {@link Claims} to a {@link SecurityToken}.
 *
 * @author Sparky Studios.
 */
@Data
public class JwtSecurityTokenAdapter implements SecurityToken {

    private final Claims claims;
    private final String token;

    @Override
    public String getId() {
        return claims.getId();
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return "bearer";
    }

    @Override
    public String getUsername() {
        return claims.getSubject();
    }

    @Override
    public long getUserId() {
        return claims.get("userId", Long.class);
    }

    @Override
    public boolean isVerified() {
        return claims.get("verified", Boolean.class);
    }

    @Override
    public LocalDateTime getIssuedAt() {
        return claims.getIssuedAt()
                .toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    @Override
    public LocalDateTime getExpiry() {
        return claims.getExpiration()
                .toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    @Override
    public GrantedAuthority getRole() {
        return new SimpleGrantedAuthority(claims.get("role", String.class));
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return ((Collection<String>) claims.get("scope"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
