package com.sparky.trak.gateway.server.adapter;

import com.sparky.trak.gateway.server.config.JwtConfig;
import com.sparky.trak.gateway.server.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@EnableWebSecurity
public class JwtConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private final JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterAfter(new JwtAuthenticationFilter(jwtConfig), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, jwtConfig.getAuthUri())
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/auth/users")
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/auth/users/recover")
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/email-management/v1/emails/verification")
                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/email-management/v1/emails/recovery")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/image-management/v1/images/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/game-management/v1/games/**/image")
                .permitAll()
                .anyRequest()
                .authenticated();
    }
}
