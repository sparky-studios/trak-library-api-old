package com.sparkystudios.traklibrary.gateway.server.config;

import com.sparkystudios.traklibrary.security.configuration.JwtConfig;
import com.sparkystudios.traklibrary.security.filter.JwtAuthorizationFilter;
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
                    .antMatchers("/api/auth/**", "/api/games/**", "/api/images/**", "/api/notifications/**")
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/auth", "/api/auth/users").permitAll()
                    .antMatchers(HttpMethod.PUT, "/api/auth/users", "/api/auth/users/recover").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/games/**/image").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .cors()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                    .and()
                    .csrf().disable()
                    .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtConfig));
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
                    .antMatcher("/api/emails/**")
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .cors()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .and()
                    .csrf().disable();
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
