package com.sparky.trak.game.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationService {

    Authentication getAuthentication();

    boolean isCurrentAuthenticatedUser(long userId);
}
