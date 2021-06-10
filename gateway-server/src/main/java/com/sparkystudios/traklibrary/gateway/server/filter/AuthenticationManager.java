package com.sparkystudios.traklibrary.gateway.server.filter;

import com.google.common.base.Strings;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.StringReader;
import java.security.Key;
import java.util.List;

@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Key publicKey;

    public AuthenticationManager(String publicKeyText) {
        publicKey = readPublicKey(publicKeyText);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        // Get the current credentials, which should be the JWT at this stage.
        var token = authentication.getCredentials().toString();

        try {
            // Validate the token.
            var claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();

            if (!Strings.isNullOrEmpty(username)) {
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claims.get("role", String.class)));

                // Create the authenticated user, which stores some details about the user that made the request.
                var authenticatedUserDto = new AuthenticatedUserDto();
                authenticatedUserDto.setUserId(claims.get("userId", Long.class));
                authenticatedUserDto.setVerified(claims.get("verified", Boolean.class));
                authenticatedUserDto.setToken(token);

                // Create the authenticated object, which includes the username and the authorities associated with the user.
                var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(authenticatedUserDto);

                // Authentication the user.
                SecurityContextHolder.getContext().setAuthentication(auth);

                return Mono.just(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        return Mono.empty();
    }

    private Key readPublicKey(String publicKeyText) {
        try (var stringReader = new StringReader(publicKeyText)) {
            var pemParser = new PEMParser(stringReader);
            var publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());

            return new JcaPEMKeyConverter().getPublicKey(publicKeyInfo);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read public key for JWT decryption.", e);
        }
    }

    @Data
    public static class AuthenticatedUserDto {

        private long userId;

        private boolean verified;

        private String token;
    }
}
