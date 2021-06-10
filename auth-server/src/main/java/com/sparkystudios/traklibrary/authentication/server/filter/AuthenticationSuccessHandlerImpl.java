package com.sparkystudios.traklibrary.authentication.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.authentication.service.TokenService;
import com.sparkystudios.traklibrary.authentication.service.dto.TokenPayloadDto;
import com.sparkystudios.traklibrary.security.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Simple handler class that is invoked by the {@link AuthenticationProcessingFilter} when an authentication request is successful.
 *
 * @author Tucasi Ltd.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Value("${trak.security.jwt.expiry-time}")
    private long expiryTime;

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    /**
     * Invoked by the {@link AuthenticationProcessingFilter} when an authentication request succeeds. When a
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
        var userContext = (UserContext) authentication.getPrincipal();

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
            throw new AuthenticationServiceException("Cannot authenticate for a user with no authorities.");
        }

        Collection<String> authorities = userContext.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Ensure that the authorities contain at least a single role.
        String role = authorities.stream()
                .filter(authority -> authority.startsWith("ROLE"))
                .findAny()
                .orElseThrow(() -> new AuthenticationServiceException("Cannot create an access token for a user with no role."));

        Iterable<String> scopes = authorities.stream()
                .filter(a -> !a.startsWith("ROLE"))
                .collect(Collectors.toList());

        // Create the access and refresh tokens.
        String accessToken = tokenService.createAccessToken(userContext, role, scopes);
        String refreshToken = tokenService.createRefreshToken(userContext);

        // Place the tokens in a payload to return them as a sensibly serialized type to the user.
        var tokenPayloadDto = new TokenPayloadDto();
        tokenPayloadDto.setAccessToken(accessToken);
        tokenPayloadDto.setTokenType("bearer");
        tokenPayloadDto.setExpiresIn(LocalDateTime.now().plusSeconds(expiryTime / 1000).toEpochSecond(ZoneOffset.UTC));
        tokenPayloadDto.setRefreshToken(refreshToken);
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
