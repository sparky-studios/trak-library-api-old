package com.sparkystudios.traklibrary.security.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("isAuthenticated() and hasAnyRole('MODERATOR', 'ADMIN') and (hasRole('ADMIN') or hasAuthority('games/publisher.write'))")
public @interface AllowedForModeratorWithPublisherWriteAuthority {
}
