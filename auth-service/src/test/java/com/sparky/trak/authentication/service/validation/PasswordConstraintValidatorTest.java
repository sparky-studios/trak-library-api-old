package com.sparky.trak.authentication.service.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidatorTest {

    @Test
    public void isValid_withPasswordLessThan8Characters_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "pwd";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains less than 8 characters.");
    }

    @Test
    public void isValid_withPasswordMoreThan30Characters_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "thisisapasswordwhichwillprobablycontainmorethanthirtycharacterswhichisntallowed";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains more than 30 characters.");
    }

    @Test
    public void isValid_withPasswordWithNoUppercase_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "passwordone";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains no uppercase characters.");
    }

    @Test
    public void isValid_withPasswordWithNoLowercase_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "PASSWORDONE";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains no lowercase characters.");
    }

    @Test
    public void isValid_withPasswordWithNoNumbers_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "PasswordOne";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains no numbers.");
    }

    @Test
    public void isValid_withPasswordWithWhitespace_returnsFalse() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = " PasswordOne ";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertFalse(result, "Should be false if the password contains whitespace.");
    }

    @Test
    public void isValid_withValidPassword_returnsTrue() {
        // Arrange
        ConstraintValidatorContext constraintValidatorContextMock = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(ArgumentMatchers.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        String password = "Password123";

        // Act
        boolean result = new PasswordConstraintValidator().isValid(password, constraintValidatorContextMock);

        // Assert
        Assertions.assertTrue(result, "Should be true if the password passes all the rules.");
    }
}
