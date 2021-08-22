package com.sparkystudios.traklibrary.authentication.server.provider;

import com.sparkystudios.traklibrary.authentication.service.dto.UserDto;
import com.sparkystudios.traklibrary.security.token.data.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The {@link UsernamePasswordAuthenticationProvider} is an authentication provider that replaces the
 * default {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} provided
 * by Spring and focuses more on just retrieving user information that are mapped to the given
 * credentials instead of focusing on additional behavior, such as remember me services and user
 * caching.
 *
 * @author Sparky Studios
 * @since 0.1.0
 */
@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    /**
     * Checks whether the credentials contained within the {@link Authentication} are mapped against a
     * valid user in the database and that they have valid roles and authorities assigned to them. If authentication
     * was successful, a {@link UsernamePasswordAuthenticationToken} instance is passed along the authentication
     * process containing a {@link com.sparkystudios.traklibrary.security.token.data.UserData} for user data and
     * their associated authorities.
     *
     * If the credentials are incorrect, a {@link BadCredentialsException} exception is thrown and if they
     * have no valid authorities, a {@link InsufficientAuthenticationException} exception is thrown and
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
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        // Check to see if the credentials are linked to an existing user, if not it'll throw
        // a username not found exception.
        var userDto = (UserDto)userDetailsService.loadUserByUsername(username);

        // Ensure the password is correct.
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            String errorMessage = messageSource
                    .getMessage("authentication.exception.bad-credentials", new Object[] {}, LocaleContextHolder.getLocale());

            throw new BadCredentialsException(errorMessage);
        }

        // This should never happen, but highlights an issue where there are no defined roles in the
        // database.
        if (userDto.getAuthorities() == null || userDto.getAuthorities().isEmpty()) {
            String errorMessage = messageSource
                    .getMessage("authentication.exception.insufficient-roles", new Object[] {}, LocaleContextHolder.getLocale());

            throw new InsufficientAuthenticationException(errorMessage);
        }

        // Create the user context, which contains hand-picked user data to pass between services.
        var userData = new UserData();
        userData.setUserId(userDto.getId());
        userData.setUsername(userDto.getUsername());
        userData.setVerified(userDto.isVerified());
        userData.setUsing2fa(userDto.isUsingTwoFactorAuthentication());
        userData.setAuthorities(userDto.getAuthorities());

        // Create another username and password token with authority information that can be
        // responded to further down the chain.
        return new UsernamePasswordAuthenticationToken(userData, null, userData.getAuthorities());
    }

    /**
     * Flags that the {@link UsernamePasswordAuthenticationProvider} will only be executed if the authentication
     * type provided is assignable to a {@link UsernamePasswordAuthenticationToken}.
     *
     * @param aClass The class type to check for assignability.
     *
     * @return True if the argument is assignable to a {@link UsernamePasswordAuthenticationToken}.
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
