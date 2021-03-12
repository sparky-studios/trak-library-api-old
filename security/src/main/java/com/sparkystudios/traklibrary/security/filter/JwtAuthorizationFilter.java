package com.sparkystudios.traklibrary.security.filter;

import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.security.configuration.JwtConfig;
import com.sparkystudios.traklibrary.security.dto.AuthenticatedUserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtConfig jwtConfig;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        super(authenticationManager);
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Grab the auth header from the request.
        String header = httpServletRequest.getHeader("Authorization");

        // If no authorization has been provided, just carry on down the request filter chain.
        if (Strings.isNullOrEmpty(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // If no token if provided the user won't be authenticated, but at this point they may be trying to access a publicly accessible path.
        String token = header.replace("Bearer ", "");

        try {
            // Validate the token.
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecretKey().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            if (!Strings.isNullOrEmpty(username)) {
                @SuppressWarnings("unchecked")
                List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("scopes"))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Create the authenticated user, which stores some details about the user that made the request.
                AuthenticatedUserDto authenticatedUserDto = new AuthenticatedUserDto();
                authenticatedUserDto.setUserId(claims.get("userId", Long.class));
                authenticatedUserDto.setVerified(claims.get("verified", Boolean.class));
                authenticatedUserDto.setToken(token);

                // Create the authenticated object, which includes the username and the authorities associated with the user.
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(authenticatedUserDto);

                // Authentication the user.
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        // Carry on the filter chain after authentication is done.
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
