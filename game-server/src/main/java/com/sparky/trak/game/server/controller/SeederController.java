package com.sparky.trak.game.server.controller;

import com.sparky.trak.game.server.annotation.AllowedForAdmin;
import com.sparky.trak.game.server.seeder.SeederRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Profile({ "test", "development" })
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/seed", produces = MediaTypes.HAL_JSON_VALUE)
public class SeederController {

    private final SeederRunner seederRunner;

    /**
     * Development-centered endpoint that can be used to seed any game database with a collection of randomly
     * generated seeded data. It should only be used on a database that doesn't already contain any data, as
     * seeding a database with existing records could lead to undesired behaviour.
     *
     * Although the end-point does generate data, none is returned to the callee, so the response is flagged
     * as a 204 (NO_CONTENT). The only way to tell if the seeding failed, is if the process throws an exception
     * during generation, which will be returned as a standard {@link com.sparky.trak.game.server.exception.ApiError},
     * depending on the exception type.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void seed(Principal principal) {
        seederRunner.run();
    }
}
