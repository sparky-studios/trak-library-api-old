package com.sparkystudios.traklibrary.notification.server.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("isAuthenticated() and hasRole('MODERATOR')")
public @interface AllowedForModerator {
}
