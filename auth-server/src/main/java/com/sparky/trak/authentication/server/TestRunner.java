package com.sparky.trak.authentication.server;

import com.sparky.trak.authentication.domain.User;
import com.sparky.trak.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        User user = new User();
        user.setUsername("superuser");
        user.setEmailAddress("user@trak.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setActive(true);

        userRepository.save(user);
    }
}
