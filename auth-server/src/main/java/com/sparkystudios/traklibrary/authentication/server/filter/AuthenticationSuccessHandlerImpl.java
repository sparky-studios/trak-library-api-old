package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.authentication.service.factory.JwtFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Simple handler class that is invoked by the {@link AuthenticationProcessingFilter} when an authentication request is successful.
 *
 * @author Tucasi Ltd.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JwtFactory jwtFactory;
    private final ObjectMapper objectMapper;

    /**
     * Invoked by the {@link AuthenticationProcessingFilter} when an authentication request succeeds. When a
     * request is successful, a JWT access token and refresh token will be generated and written to the response
     * body for use.
     *
     * @param httpServletRequest The {@link HttpServletRequest} instance of the request.
     * @param httpServletResponse The {@link HttpServletResponse} instance of the request.
     * @param authentication The authentication token used for successful authentication.
     *
     * @throws IOException Thrown if the {@link TokenPayloadDto} instance cannot be written to the response.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // We'll need to retrieve user data to add to the token.
        UserDto userDto = (UserDto) authentication.getPrincipal();

        // Create the access and refresh tokens.
        String accessToken = jwtFactory.createAccessToken(userDto);
        String refreshToken = jwtFactory.createRefreshToken(userDto);

        // Place the tokens in a payload to return them as a sensibly serialized type to the user.
        TokenPayloadDto tokenPayloadDto = new TokenPayloadDto();
        tokenPayloadDto.setAccessToken(accessToken);
        tokenPayloadDto.setRefreshToken(refreshToken);

        // Create a response that returns the jwt token.
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(httpServletResponse.getWriter(), tokenPayloadDto);

        // Remove any temporary authentication related data which may have been stored within the session.
        HttpSession httpSession = httpServletRequest.getSession();

        if (httpSession != null) {
            httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
