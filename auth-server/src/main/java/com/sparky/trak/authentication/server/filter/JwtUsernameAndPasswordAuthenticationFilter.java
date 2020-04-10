package com.sparky.trak.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparky.trak.authentication.server.config.JwtConfig;
import com.sparky.trak.authentication.service.dto.UserCredentialsDto;
import com.sparky.trak.authentication.service.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;

        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getAuthUri()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserCredentialsDto userCredentialsDto = new ObjectMapper().readValue(request.getInputStream(), UserCredentialsDto.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userCredentialsDto.getUsername(), userCredentialsDto.getPassword(), Collections.emptyList());

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) {

        long now = System.currentTimeMillis();
        String token = Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .claim("userId", ((UserDto)auth.getPrincipal()).getId())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + (24 * 60 * 60 * 1000)))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey().getBytes())
                .compact();

        // Add token to header
        response.addHeader("Authorization", "Bearer " + token);
    }
}
