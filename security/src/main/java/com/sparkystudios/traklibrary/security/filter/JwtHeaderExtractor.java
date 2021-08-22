package com.sparkystudios.traklibrary.security.filter;

import com.google.common.base.Strings;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

/**
 * Class that is responsible for extracting a bearer token out of the authorization header
 * of a given request.
 *
 * @author Sparky Studios
 * @since 0.1.0
 */
@Component
public class JwtHeaderExtractor {

    private static final String HEADER_PREFIX = "Bearer ";

    /**
     * Given the value of an Authorization header. This method will attempt to extract the token
     * value whilst performing some additional validation to ensure that the authorization token
     * has been provided in the correct format. If the format is incorrect, {@link AuthenticationServiceException}'s
     * will be thrown, otherwise it will return the token, with the 'Bearer ' prefix removed.
     *
     * @param header The value to extract the bearer token from.
     *
     * @return The token contained within the header, if correctly formatted.
     */
    public String extract(String header) {
        if (Strings.isNullOrEmpty(header)) {
            throw new AuthenticationServiceException("The authorization header is invalid.");
        }

        if (!header.startsWith(HEADER_PREFIX)) {
            throw new AuthenticationServiceException("The authorization header is not prefixed with 'Bearer'");
        }

        return header.substring(HEADER_PREFIX.length());
    }
}
