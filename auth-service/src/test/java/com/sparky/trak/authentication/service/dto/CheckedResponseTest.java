package com.sparky.trak.authentication.service.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckedResponseTest {
    
    @Test
    public void constructor_withNoErrorMessage_errorIsFlaggedAsFalse() {
        // Arrange
        Integer value = 5;
        
        // Act
        CheckedResponse<Integer> result = new CheckedResponse<>(value);
        
        // Assert
        Assertions.assertEquals(value, result.getData(), "The data should be equal to the value provided.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty by default.");
        Assertions.assertFalse(result.isError(), "The error flag should be set to false if no error was provided.");
    }

    @Test
    public void constructor_withEmptyErrorMessage_errorIsFlaggedAsFalse() {
        // Arrange
        Integer value = 5;

        // Act
        CheckedResponse<Integer> result = new CheckedResponse<>(value, "");

        // Assert
        Assertions.assertEquals(value, result.getData(), "The data should be equal to the value provided.");
        Assertions.assertEquals("", result.getErrorMessage(), "The error message should be empty if specified as empty.");
        Assertions.assertFalse(result.isError(), "The error flag should be set to false if no error was provided.");
    }

    @Test
    public void constructor_withErrorMessage_errorIsFlaggedAsTrue() {
        // Arrange
        Integer value = 5;
        String errorMessage = "error-message";

        // Act
        CheckedResponse<Integer> result = new CheckedResponse<>(value, errorMessage);

        // Assert
        Assertions.assertEquals(value, result.getData(), "The data should be equal to the value provided.");
        Assertions.assertEquals(errorMessage, result.getErrorMessage(), "The error message should be equal to the one provided.");
        Assertions.assertTrue(result.isError(), "The error flag should be set to true if an error was provided.");
    }
}
