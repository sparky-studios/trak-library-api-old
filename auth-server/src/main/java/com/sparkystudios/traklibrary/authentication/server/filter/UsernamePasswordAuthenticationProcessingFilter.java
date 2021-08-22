package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.security.exception.AuthenticationMethodNotSupportedException;
import com.sparkystudios.traklibrary.authentication.service.dto.LoginRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Processing filter that is used within the Spring Security implementation to process requests made
 * to our authentication services utilising JWT.
 *
 * @author Sparky Studios
 */
@Slf4j
public class UsernamePasswordAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    /**
     * Default constructor used to inject the needed dependencies.
     *
     * @param authenticationManager The {@link AuthenticationManager} instance to inject.
     * @param authenticationSuccessHandler The {@link AuthenticationSuccessHandler} instance to inject.
     * @param authenticationFailureHandler The {@link AuthenticationFailureHandler} instance to inject.
     * @param messageSource The {@link MessageSource} instance to inject.
     * @param objectMapper The {@link ObjectMapper} instance to inject.
     */
    public UsernamePasswordAuthenticationProcessingFilter(AuthenticationManager authenticationManager,
                                                          AuthenticationSuccessHandler authenticationSuccessHandler,
                                                          AuthenticationFailureHandler authenticationFailureHandler,
                                                          MessageSource messageSource,
                                                          ObjectMapper objectMapper) {
        // Responds to any POST request made to /token
        super(new AntPathRequestMatcher("/token", HttpMethod.POST.name()), authenticationManager);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    /**
     * Attempts authentication with the credentials given in the {@link LoginRequestDto} request body. Authentication
     * within this filter will retrieve the username and password credentials from the {@link LoginRequestDto} and attempt
     * to generate a JWT if the credentials match a user within the system.
     *
     * The method will ensure that the request has been made to the correct endpoint and the information provided is not invalid,
     * if any invalidity is found, the method will throw an {@link AuthenticationException} and return a 401.
     *
     * If authentication is successful and the user is using 2FA as their authentication method, they wil be given a JWT that will
     * only permission them to access the 2FA endpoint for additional authentication, otherwise a JWT and refresh token will be returned
     * to the client for use with authenticated elements of the API.
     *
     * @param httpServletRequest The {@link HttpServletRequest} instance of the request.
     * @param httpServletResponse The {@link HttpServletResponse} instance of the request.
     *
     * @return An implementation of the {@link Authentication}, which will be {@link UsernamePasswordAuthenticationToken}.
     *
     * @throws AuthenticationException Thrown if authentication fails.
     * @throws IOException Thrown if the body of the request cannot be read.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException {
        // Ensure that the authentication request can only be a POST request.
        if (!HttpMethod.POST.name().equals(httpServletRequest.getMethod())) {
            throw new AuthenticationMethodNotSupportedException(messageSource
                    .getMessage("authentication.exception.method-not-supported", new Object[] {httpServletRequest.getMethod()}, LocaleContextHolder.getLocale()));
        }

        // Retrieve the login request which is supplied as the request body.
        var loginRequestDto = objectMapper.readValue(httpServletRequest.getReader(), LoginRequestDto.class);

        // Trim any whitespace from the trailing ends of the credentials.
        String username = Strings.nullToEmpty(loginRequestDto.getUsername()).trim();
        String password = Strings.nullToEmpty(loginRequestDto.getPassword()).trim();

        // Ensure the credentials provided are valid.
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
            throw new AuthenticationServiceException(messageSource
                    .getMessage("authentication.exception.missing-credentials", new Object[] {}, LocaleContextHolder.getLocale()));
        }

        // Create the authentication token to try and generate a JWT for.
        var usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        // Attempt authentication.
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }

    /**
     * Invoked when authentication is successful. It will propagate the handling to the chosen {@link AuthenticationSuccessHandler}
     * implementation.
     *
     * @param request The {@link HttpServletRequest} instance of the request.
     * @param response The {@link HttpServletResponse} instance of the request.
     * @param chain The filter chain.
     * @param authResult The {@link Authentication} successful result.
     *
     * @throws IOException Thrown if the response can't be written to.
     * @throws ServletException Thrown if there is an internal error within the servlet filter chain.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }

    /**
     * Invoked when authentication is unsuccessful. It will propagate the handling to the chosen {@link AuthenticationFailureHandler}
     * implementation.
     *
     * @param request The {@link HttpServletRequest} instance of the request.
     * @param response The {@link HttpServletResponse} instance of the request.
     * @param failed The exception that caused the unsuccessful authentication.
     *
     * @throws IOException Thrown if the response can't be written to.
     * @throws ServletException Thrown if there is an internal error within the servlet filter chain.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}
