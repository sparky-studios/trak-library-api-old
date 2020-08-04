package com.sparky.trak.gateway.server.config;

import com.sparky.trak.gateway.server.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Order(1)
    @Configuration
    @RequiredArgsConstructor
    public static class JwtSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        private final JwtConfig jwtConfig;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .requestMatchers()
                    .antMatchers("/auth/**", "/api/game-management/**", "/api/image-management/**", "/api/notification-management/**")
                    .and()
                    .cors()
                    .and()
                    .csrf().disable()
                    .exceptionHandling().authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, jwtConfig.getAuthUri())
                    .permitAll()
                    .antMatchers(HttpMethod.PUT, "/auth/users")
                    .permitAll()
                    .antMatchers(HttpMethod.PUT, "/auth/users/recover")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/image-management/v1/images/**")
                    .permitAll()
                    .antMatchers(HttpMethod.GET, "/api/game-management/v1/games/**/image")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtConfig))
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    public static class BasicSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Value("${trak.security.user.email.username}")
        private String username;

        @Value("${trak.security.user.email.password}")
        private String password;

        private final MyBasicAuthenticationEntryPoint authenticationEntryPoint;
        private final PasswordEncoder passwordEncoder;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/email-management/**")
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser(username)
                    .password(passwordEncoder.encode(password))
                    .authorities("ROLE_EMAIL_USER");
        }
    }
}
