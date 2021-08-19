package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.data.SecurityToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import com.sparkystudios.traklibrary.security.token.data.UserSecurityRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Simple handler class that is invoked by the {@link UsernamePasswordAuthenticationProcessingFilter} when an authentication request is successful.
 *
 * @author Tucasi Ltd.
 */
@Component
@Qualifier("usernamePasswordAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final SecurityTokenService securityTokenService;
    private final ObjectMapper objectMapper;

    /**
     * Invoked by the {@link UsernamePasswordAuthenticationProcessingFilter} when an authentication request succeeds. When a
     * request is successful, a JWT access token and refresh token will be generated and written to the response
     * body for use.
     *
     * @param request The {@link HttpServletRequest} instance of the request.
     * @param response The {@link HttpServletResponse} instance of the request.
     * @param authentication The authentication token used for successful authentication.
     *
     * @throws IOException Thrown if the {@link TokenPayloadDto} instance cannot be written to the response.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // We'll need to retrieve user data to add to the token.
        var userData = (UserData) authentication.getPrincipal();

        // Ensure that the authorities contain at least a single role.
        UserSecurityRole role = userData.getAuthorities()
                .stream()
                .filter(a -> a.getAuthority().startsWith("ROLE_"))
                .map(a -> UserSecurityRole.valueOf(a.getAuthority()))
                .findAny()
                .orElseThrow(() -> new AuthenticationServiceException("Cannot create an access token for a user with no role."));

        Iterable<String> scopes = userData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .collect(Collectors.toList());

        // Create the access and refresh tokens.
        SecurityToken accessToken;
        SecurityToken refreshToken = null;

        if (userData.isUsing2fa()) {
            accessToken = securityTokenService.createTwoFactorAuthenticationToken(userData);
        } else {
            accessToken = securityTokenService.createAccessToken(userData, role, scopes);
            refreshToken = securityTokenService.createRefreshToken(userData);
        }

        // Place the tokens in a payload to return them as a sensibly serialized type to the user.
        var tokenPayloadDto = new TokenPayloadDto();
        tokenPayloadDto.setTokenType(accessToken.getType());
        tokenPayloadDto.setIssuedAt(accessToken.getIssuedAt());
        tokenPayloadDto.setExpiresAt(accessToken.getExpiry());
        tokenPayloadDto.setAccessToken(accessToken.getToken());
        tokenPayloadDto.setRefreshToken(refreshToken != null ? refreshToken.getToken() : "");
        tokenPayloadDto.setScope(String.join(";", scopes));

        // Create a response that returns the jwt token.
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(objectMapper.writeValueAsString(tokenPayloadDto));

        // Remove any temporary authentication related data which may have been stored within the session.
        var httpSession = request.getSession();

        if (httpSession != null) {
            httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
