package com.sparkystudios.traklibrary.notification.server.adapter;

import com.sparkystudios.traklibrary.security.filter.JwtAuthenticationProcessingFilter;
import com.sparkystudios.traklibrary.security.filter.JwtHeaderExtractor;
import com.sparkystudios.traklibrary.security.filter.SkipPathRequestMatcher;
import com.sparkystudios.traklibrary.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RequiredArgsConstructor
@EnableWebSecurity
public class NotificationServerSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final JwtHeaderExtractor jwtHeaderExtractor;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SkipPathRequestMatcher skipPathRequestMatcher =
                new SkipPathRequestMatcher(Collections.singletonList(new AntPathRequestMatcher("/non-existent-path")), "/**");

        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter =
                new JwtAuthenticationProcessingFilter(authenticationManager(), authenticationFailureHandler, jwtHeaderExtractor, skipPathRequestMatcher);

        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .csrf().disable()
                .addFilterBefore(jwtAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(jwtAuthenticationProvider);
    }
}
