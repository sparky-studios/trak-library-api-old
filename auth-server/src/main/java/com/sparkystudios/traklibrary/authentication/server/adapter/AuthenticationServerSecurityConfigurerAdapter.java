package com.sparkystudios.traklibrary.authentication.server.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.server.filter.JwtAuthenticationFilter;
import com.sparkystudios.traklibrary.security.configuration.JwtConfig;
import com.sparkystudios.traklibrary.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class AuthenticationServerSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/", "/users").permitAll()
                .antMatchers(HttpMethod.PUT, "/users/recover", "/users").permitAll()
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
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), objectMapper, jwtConfig))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtConfig));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
