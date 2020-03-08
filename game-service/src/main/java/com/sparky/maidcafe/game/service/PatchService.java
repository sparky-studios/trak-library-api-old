package com.sparky.maidcafe.game.service;

import javax.json.JsonMergePatch;

public interface PatchService {

    /**
     * Given a {@link JsonMergePatch}, this method will attempt to apply the JSON data within the patch to the
     * given target instance and return the patched result. The elements on the target will only be updated if the
     * {@link JsonMergePatch} contains JSON that matches any of the fields on the target class, new fields will not
     * be added to the target instance. If the target has any annotated fields requiring validation, the patch will
     * check after application, if not all validation conditions have been met a {@link javax.validation.ConstraintViolationException}
     * will be thrown.
     *
     * @param jsonMergePatch The {@link JsonMergePatch} containing JSON data to apply to the target.
     * @param target The target instance to patch.
     * @param clazz The class type of the target.
     * @param <T> The generic target type being patched.
     *
     * @return The patched target as a new instance.
     */
    <T> T patch(JsonMergePatch jsonMergePatch, T target, Class<T> clazz);
}
