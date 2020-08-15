package com.traklibrary.game.server.adapter;

import com.traklibrary.game.server.configuration.JwtConfig;
import com.traklibrary.game.server.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@EnableWebSecurity
public class GameServerSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**/image").permitAll()
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
