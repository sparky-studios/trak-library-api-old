package com.sparkystudios.traklibrary.gateway.server.config;

import com.sparkystudios.traklibrary.gateway.server.filter.AuthenticationManager;
import com.sparkystudios.traklibrary.gateway.server.filter.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${trak.security.jwt.secret-key}")
    private String secretKey;

    @Order(1)
    @Bean
    SecurityWebFilterChain jwtSecurityWebFilterChain(ServerHttpSecurity http) {

        AuthenticationManager authenticationManager = new AuthenticationManager(secretKey);
        SecurityContextRepository securityContextRepository = new SecurityContextRepository(authenticationManager);

        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**", "/games/**", "/images/**", "/notifications/**"))
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.POST, "/auth/token", "/auth/token/**", "/auth/users").permitAll()
                .pathMatchers(HttpMethod.PUT, "/auth/users", "/auth/users/recover").permitAll()
                .pathMatchers(HttpMethod.GET, "/images/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/games/*/images/small", "/games/*/images/medium", "/games/*/images/large").permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .cors()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(((serverWebExchange, e) -> Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))))
                .accessDeniedHandler(((serverWebExchange, e) -> Mono.fromRunnable(() -> serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))))
                .and()
                .csrf().disable();

        return http.build();
    }

    @Order(2)
    @Bean
    SecurityWebFilterChain basicSecurityWebFilterChain(ServerHttpSecurity http) {
        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/emails/**"))
                .authorizeExchange()
                .pathMatchers("/emails/**").hasRole("EMAIL_USER")
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()
                .csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(@Value("${trak.security.user.email.username}") String username,
                                                            @Value("${trak.security.user.email.password}") String password,
                                                            PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("EMAIL_USER")
                .build();

        return new MapReactiveUserDetailsService(user);
    }
}
