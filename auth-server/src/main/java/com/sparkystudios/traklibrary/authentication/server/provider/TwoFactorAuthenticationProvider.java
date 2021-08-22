package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.token.SecurityTokenService;
import com.sparkystudios.traklibrary.security.token.authentication.TwoFactorAuthenticationToken;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import dev.samstevens.totp.code.CodeVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * The {@link TwoFactorAuthenticationProvider} is an authentication provider that is used by any micro-service
 * that protects resources behind authentication that requires a second level of authentication, such as codes generated
 * by TOTP. Its purpose is to perform validation and authorization whenever a {@link TwoFactorAuthenticationToken}
 * is provided as the authentication token type.
 *
 * @author Sparky Studios
 * @since 0.1.0
 */
@Component
@RequiredArgsConstructor
public class TwoFactorAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final SecurityTokenService securityTokenService;
    private final CodeVerifier codeVerifier;
    private final MessageSource messageSource;

    /**
     * Checks whether the credentials contained within the {@link Authentication} contain a jwt that is valid
     * and has the correct role assigned. It will also check that the code provided with their 2FA request matches
     * the secret stored against the user. If authentication is successful, a {@link TwoFactorAuthenticationToken}
     * instance is passed along the authentication process containing a {@link UserData} for user data and their
     * associated authorities.
     *
     * If the JWT is invalid or the 2FA code is incorrect, a {@link BadCredentialsException} exception is thrown and
     * bubbled up the stack.
     *
     * @param authentication The {@link UsernamePasswordAuthenticationToken} to attempt authentication with.
     *
     * @return A {@link UsernamePasswordAuthenticationToken} with additional user data.
     *
     * @throws AuthenticationException Thrown if authentication fails or invalid data is provided.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Retrieve the credentials passed into the token.
        String jwt = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();

        // Try to parse the JWT, if it fails an exception will be bubbled up the stack.
        var securityToken = securityTokenService.getToken(jwt);

        // Check to see if the credentials are linked to an existing user, if not it'll throw
        // a username not found exception.
        var userDto = (UserDto) userDetailsService.loadUserByUsername(securityToken.getUsername());

        // Ensure the secret provided by the 2FA is correct.
        if (!codeVerifier.isValidCode(userDto.getTwoFactorAuthenticationSecret(), code)) {
            String errorMessage = messageSource
                    .getMessage("authentication.exception.bad-2fa-credentials", new Object[] {}, LocaleContextHolder.getLocale());

            throw new BadCredentialsException(errorMessage);
        }

        // Create the user data, which contains hand-picked user information to pass between services.
        var userData = new UserData();
        userData.setUserId(userDto.getId());
        userData.setUsername(userDto.getUsername());
        userData.setVerified(userDto.isVerified());
        userData.setUsing2fa(userDto.isUsingTwoFactorAuthentication());
        userData.setAuthorities(userDto.getAuthorities());

        // Create another username and password token with authority information that can be
        // responded to further down the chain.
        return new TwoFactorAuthenticationToken(userData, null, userData.getAuthorities());
    }

    /**
     * Flags that the {@link TwoFactorAuthenticationToken} will only be executed if the authentication
     * type provided is assignable to a {@link TwoFactorAuthenticationToken}.
     *
     * @param aClass The class type to check for assignability.
     *
     * @return True if the argument is assignable to a {@link TwoFactorAuthenticationToken}.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return TwoFactorAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
