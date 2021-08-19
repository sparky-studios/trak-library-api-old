package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
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
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Simple handler class that is invoked by the {@link TwoFactorAuthenticationProcessingFilter} when an authentication request with a 2FA
 * code is successful.
 *
 * @since 0.1.0
 * @author Sparky Studios.
 */
@Component
@Qualifier("twoFactorAuthenticationSuccessHandler")
@RequiredArgsConstructor
public class TwoFactorAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final SecurityTokenService securityTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        // We'll need to retrieve user data to add to the token.
        var userData = (UserData) authentication.getPrincipal();

        Collection<String> authorities = userData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Ensure that the authorities contain at least a single role.
        UserSecurityRole role = authorities.stream()
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(UserSecurityRole::valueOf)
                .findAny()
                .orElseThrow(() -> new AuthenticationServiceException("Cannot create an access token for a user with no role."));

        Iterable<String> scopes = authorities.stream()
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());

        // Create the access and refresh tokens.
        var accessToken = securityTokenService.createAccessToken(userData, role, scopes);
        var refreshToken = securityTokenService.createRefreshToken(userData);

        // Place the tokens in a payload to return them as a sensibly serialized type to the user.
        var tokenPayloadDto = new TokenPayloadDto();
        tokenPayloadDto.setTokenType(accessToken.getType());
        tokenPayloadDto.setAccessToken(accessToken.getToken());
        tokenPayloadDto.setExpiresAt(accessToken.getExpiry());
        tokenPayloadDto.setRefreshToken(refreshToken.getToken());
        tokenPayloadDto.setScope(String.join(";", scopes));

        // Create a response that returns the jwt token.
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(tokenPayloadDto));

        // Remove any temporary authentication related data which may have been stored within the session.
        var httpSession = httpServletRequest.getSession();

        if (httpSession != null) {
            httpSession.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
