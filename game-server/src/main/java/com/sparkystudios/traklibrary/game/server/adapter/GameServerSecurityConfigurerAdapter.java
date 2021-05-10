package com.sparkystudios.traklibrary.game.server.adapter;

import com.sparkystudios.traklibrary.security.filter.JwtAuthenticationProcessingFilter;
import com.sparkystudios.traklibrary.security.filter.JwtHeaderExtractor;
import com.sparkystudios.traklibrary.security.filter.SkipPathRequestMatcher;
import com.sparkystudios.traklibrary.security.provider.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
public class GameServerSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final JwtHeaderExtractor jwtHeaderExtractor;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        List<RequestMatcher> pathsToSkip = new ArrayList<>();
        pathsToSkip.add(new AntPathRequestMatcher("/*/image", HttpMethod.GET.name()));
        pathsToSkip.add(new AntPathRequestMatcher("/dlc/*/image", HttpMethod.GET.name()));

        var skipPathRequestMatcher = new SkipPathRequestMatcher(pathsToSkip, "/**");
        var jwtAuthenticationProcessingFilter =
                new JwtAuthenticationProcessingFilter(authenticationManager(), authenticationFailureHandler, jwtHeaderExtractor, skipPathRequestMatcher);

        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/*/image", "/dlc/*/image").permitAll()
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
