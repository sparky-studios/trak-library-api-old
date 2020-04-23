package com.sparky.trak.authentication.service;

import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void save(RegistrationRequestDto registrationRequestDto);

    boolean isVerified(String username);

    void verify(String username, short verificationCode);
}
