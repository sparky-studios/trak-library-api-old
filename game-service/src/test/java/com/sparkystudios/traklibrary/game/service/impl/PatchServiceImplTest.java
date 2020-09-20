package com.sparkystudios.traklibrary.game.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.service.dto.GameDto;
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
class PatchServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private PatchServiceImpl patchService;

    @Test
    void patch_withValueBreakingConstraints_throwsConstraintViolationException() {
        // Arrange
        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(JsonValue.class)))
                .thenReturn(Mockito.mock(JsonValue.class));

        Mockito.when(objectMapper.convertValue(ArgumentMatchers.any(), ArgumentMatchers.eq(GameDto.class)))
                .thenReturn(new GameDto());

        Mockito.when(validator.validate(ArgumentMatchers.any()))
                .thenReturn(Collections.singleton(Mockito.mock(ConstraintViolation.class)));

        JsonMergePatch jsonMergePatch = Mockito.mock(JsonMergePatch.class);
        GameDto gameDto = new GameDto();

        // Assert
        Assertions.assertThrows(ConstraintViolationException.class, () ->
                patchService.patch(jsonMergePatch, gameDto, GameDto.class));
    }

    @Test
    void patch_withValueWithMetConstraints_returnsValue() {
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
