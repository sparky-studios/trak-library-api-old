package com.sparkystudios.traklibrary.security.filter;

import com.google.common.base.Strings;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class JwtHeaderExtractor {

    private static final String HEADER_PREFIX = "Bearer ";

    public String extract(String header) {
        if (Strings.isNullOrEmpty(header)) {
            throw new AuthenticationServiceException("");
        }

        if (header.length() < HEADER_PREFIX.length() || !header.startsWith(HEADER_PREFIX)) {
            throw new AuthenticationServiceException("");
        }

        return header.substring(HEADER_PREFIX.length());
    }
}
