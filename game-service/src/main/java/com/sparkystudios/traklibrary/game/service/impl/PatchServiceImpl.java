package com.sparkystudios.traklibrary.game.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparkystudios.traklibrary.game.service.PatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PatchServiceImpl implements PatchService {

    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Override
    public <T> T patch(JsonMergePatch jsonMergePatch, T target, Class<T> clazz) {
        // Convert the target and apply the json patch to it.
        JsonValue targetJson = objectMapper.convertValue(target, JsonValue.class);
        JsonValue patchedJson = jsonMergePatch.apply(targetJson);

        // Set the new Java object with the patch information.
        var patched = objectMapper.convertValue(patchedJson, clazz);

        // Validate the patched bean.
        Set<ConstraintViolation<T>> violations = validator.validate(patched);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Return the patched object after validation.
        return patched;
    }
}
