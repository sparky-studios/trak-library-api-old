package com.sparky.trak.game.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparky.trak.game.service.dto.GameDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class PatchServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private PatchServiceImpl patchService;

    @Test
    public void patch_withValueBreakingConstraints_throwsConstraintViolationException() {
        // Arrange
        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(JsonValue.class)))
                .thenReturn(Mockito.mock(JsonValue.class));

        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(GameDto.class)))
                .thenReturn(new GameDto());

        Mockito.when(validator.validate(ArgumentMatchers.any()))
                .thenReturn(Collections.singleton(Mockito.mock(ConstraintViolation.class)));

        // Assert
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                patchService.patch(Mockito.mock(JsonMergePatch.class), new GameDto(), GameDto.class));
    }

    @Test
    public void patch_withValueWithMetConstraints_returnsValue() {
        // Arrange
        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(JsonValue.class)))
                .thenReturn(Mockito.mock(JsonValue.class));

        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(GameDto.class)))
                .thenReturn(new GameDto());

        Mockito.when(validator.validate(ArgumentMatchers.any()))
                .thenReturn(Collections.emptySet());

        // Act
        GameDto result = patchService.patch(Mockito.mock(JsonMergePatch.class), new GameDto(), GameDto.class);

        // Assert
        Assertions.assertNotNull(result, "The result should not be null when patched.");
    }
}
