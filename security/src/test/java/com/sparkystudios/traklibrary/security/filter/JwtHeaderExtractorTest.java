package com.sparkystudios.traklibrary.security.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.AuthenticationServiceException;

class JwtHeaderExtractorTest {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "Bear", "InvalidStart"})
    void extract_withIncorrectHeader_throwsAuthenticationServiceException(String value) {
        // Arrange
        JwtHeaderExtractor jwtHeaderExtractor = new JwtHeaderExtractor();

        // Assert
        Assertions.assertThatExceptionOfType(AuthenticationServiceException.class)
                .isThrownBy(() -> jwtHeaderExtractor.extract(value));
    }

    @Test
    void extract_withValidHeaderValue_retrievesToken() {
        // Arrange
        JwtHeaderExtractor jwtHeaderExtractor = new JwtHeaderExtractor();

        // Act
        String result = jwtHeaderExtractor.extract("Bearer 123");

        // Assert
        Assertions.assertThat(result).isEqualTo("123");
    }
}
