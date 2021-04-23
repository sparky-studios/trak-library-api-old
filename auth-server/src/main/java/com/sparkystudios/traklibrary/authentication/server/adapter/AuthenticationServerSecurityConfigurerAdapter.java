package com.sparkystudios.traklibrary.authentication.server.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.server.filter.AuthenticationProcessingFilter;
import com.sparkystudios.traklibrary.authentication.server.provider.RequestAuthenticationProvider;
import com.sparkystudios.traklibrary.security.filter.JwtAuthenticationProcessingFilter;
import com.sparkystudios.traklibrary.security.filter.JwtHeaderExtractor;
import com.sparkystudios.traklibrary.security.filter.SkipPathRequestMatcher;
import com.sparkystudios.traklibrary.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthenticationServerSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String USERS_PATH = "/users";

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    private final RequestAuthenticationProvider requestAuthenticationProvider;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final JwtHeaderExtractor jwtHeaderExtractor;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Create the filter used to generate JWT's based on provided user credentials.
        var authenticationProcessingFilter =
                new AuthenticationProcessingFilter(authenticationManager(), authenticationSuccessHandler, authenticationFailureHandler, messageSource, objectMapper);

        List<RequestMatcher> pathsToSkip = Arrays.asList(
                new AntPathRequestMatcher("/token", HttpMethod.POST.name()),
                new AntPathRequestMatcher("/token/**", HttpMethod.POST.name()),
                new AntPathRequestMatcher(USERS_PATH, HttpMethod.POST.name()),
                new AntPathRequestMatcher(USERS_PATH, HttpMethod.PUT.name()),
                new AntPathRequestMatcher(USERS_PATH + "/recover", HttpMethod.POST.name()));

        var skipPathRequestMatcher = new SkipPathRequestMatcher(pathsToSkip, "/**");
        var jwtAuthenticationProcessingFilter =
                new JwtAuthenticationProcessingFilter(authenticationManager(), authenticationFailureHandler, jwtHeaderExtractor, skipPathRequestMatcher);

        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/token", "/token/**", USERS_PATH).permitAll()
                .antMatchers(HttpMethod.PUT, "/users/recover", USERS_PATH).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(requestAuthenticationProvider)
                .authenticationProvider(jwtAuthenticationProvider);
    }
}
