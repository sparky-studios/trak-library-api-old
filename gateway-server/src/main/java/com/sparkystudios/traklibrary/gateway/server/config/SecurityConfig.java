package com.sparkystudios.traklibrary.gateway.server.config;

import com.sparkystudios.traklibrary.gateway.server.filter.AuthenticationManager;
import com.sparkystudios.traklibrary.gateway.server.filter.SecurityContextRepository;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorityReactiveAuthorizationManager;
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

    private final SecurityTokenService securityTokenService;

    @Order(1)
    @Bean
    SecurityWebFilterChain jwtSecurityWebFilterChain(ServerHttpSecurity http) {

        var authenticationManager = new AuthenticationManager(securityTokenService);
        var securityContextRepository = new SecurityContextRepository(authenticationManager);

        http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/**", "/games/**", "/images/**", "/notifications/**"))
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/token", "/auth/users").permitAll()
                .pathMatchers(HttpMethod.POST, "/auth/token/2fa").hasRole("TWO_FACTOR_AUTHENTICATION_TOKEN")
                .pathMatchers(HttpMethod.PUT, "/auth/users", "/auth/users/recover").permitAll()
                .pathMatchers(HttpMethod.GET, "/images/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/games/*/image", "/games/developers/*/image", "/games/dlc/*/image", "/games/platforms/*/image", "/games/publishers/*/image").permitAll()
                .anyExchange()
                .access(AuthorityReactiveAuthorizationManager.hasAnyRole("USER", "MODERATOR", "ADMIN"))
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
