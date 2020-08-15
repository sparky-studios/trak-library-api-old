package com.traklibrary.gateway.server.filter;

import com.traklibrary.gateway.server.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
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
        if (StringUtils.isBlank(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        // If no token if provided the user won't be authenticated, but at this point they may be trying to access a publicly accessible path.
        String token = header.replace("Bearer ", StringUtils.EMPTY);

        try {
            // Validate the token.
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecretKey().getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            if (StringUtils.isNotBlank(username)) {
                @SuppressWarnings("unchecked")
                List<SimpleGrantedAuthority> authorities = ((List<String>) claims.get("authorities"))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Create the authenticated object, which includes the username and the authorities associated with the user.
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                auth.setDetails(claims.get("userId"));

                // Authentication the user.
                SecurityContextHolder.getContext().setAuthentication(auth);

                long now = System.currentTimeMillis();

                // Generate a refresh token, which is a new JWT token which can be stored by the client so that
                // the user can continue to use the application whilst they're active.
                String refreshToken = Jwts.builder()
                        .setSubject(username)
                        .claim("authorities", auth.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .claim("userId", Long.parseLong(claims.get("userId").toString()))
                        .claim("verified", Boolean.parseBoolean(claims.get("verified").toString()))
                        .setIssuedAt(new Date(now))
                        .setExpiration(new Date(now + (15 * 60000))) // 15 minutes.
                        .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecretKey().getBytes())
                        .compact();

                // Add a refresh token to the header so the client doesn't have to store credentials.
                httpServletResponse.setHeader("Refresh-Token", refreshToken);
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        // Carry on the filter chain after authentication is done.
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
