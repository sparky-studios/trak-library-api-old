package com.sparky.trak.authentication.server.controller;

import com.sparky.trak.authentication.server.annotation.AllowedForUser;
import com.sparky.trak.authentication.server.response.RestResponse;
import com.sparky.trak.authentication.service.UserService;
import com.sparky.trak.authentication.service.dto.RegistrationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public void save(@Validated @RequestBody RegistrationRequestDto registrationRequestDto) {
        userService.save(registrationRequestDto);
    }

    @AllowedForUser
    @GetMapping("/users/{username}/verified")
    public RestResponse<Boolean> isVerified(@PathVariable String username) {
        return new RestResponse<>(userService.isVerified(username));
    }

    @AllowedForUser
    @PutMapping("/users/{username}/verify")
    public void verify(@PathVariable String username, @RequestParam short verificationCode) {
        userService.verify(username, verificationCode);
    }
}
