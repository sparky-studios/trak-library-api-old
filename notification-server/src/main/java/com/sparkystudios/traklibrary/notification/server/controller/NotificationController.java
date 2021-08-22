package com.sparkystudios.traklibrary.notification.server.controller;

import com.sparkystudios.traklibrary.notification.domain.MobileDeviceLink;
import com.sparkystudios.traklibrary.notification.service.MobileDeviceLinkService;
import com.sparkystudios.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;
import com.sparkystudios.traklibrary.security.annotation.AllowedForUser;
import com.sparkystudios.traklibrary.security.exception.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * The {@link NotificationController} is a simple controller class that exposes a small number of API endpoints that are used
 * to interact with the push notification functionality. It's purpose is to be used to register and unregister linked accounts
 * and dispatch notifications to any device.
 *
 * Unlike the similar email-service, all notification functionality can be accessed by any user with user credentials. No
 * elevated privileges are needed.
 *
 * @since 0.1.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.sparkystudios.traklibrary+json;version=1.0")
public class NotificationController {

    private final MobileDeviceLinkService mobileDeviceLinkService;

    /**
     * End-point that will attempt to save a {@link MobileDeviceLink} with
     * the information provided in the {@link MobileDeviceLinkRegistrationRequestDto}. This endpoint will register
     * the information both within the persistence layer and the third-party push notification provider. It
     * should be noted that links can only be registered for users that have an ID matching that of the
     * currently authenticated user.
     *
     * The {@link MobileDeviceLinkRegistrationRequestDto} request body must contain valid information, otherwise
     * the end-point will return an {@link ApiError}. An {@link ApiError} will also be returned if any issues occur
     * during persistence or third-party registration.
     *
     * @param mobileDeviceLinkRegistrationRequestDto The {@link MobileDeviceLinkRegistrationRequestDto} information to register..
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void register(@RequestBody @Validated MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto) {
        mobileDeviceLinkService.register(mobileDeviceLinkRegistrationRequestDto);
    }

    /**
     * End-point that will remove any {@link MobileDeviceLink}'s that are associated
     * with the given user ID and device ID and remove the references from the underlying third-party notification service, if needed.
     * It should be noted that links can only be unregistered for users that have an ID matching that of the currently
     * authenticated user.
     *
     * If any errors are thrown during removal, an {@link ApiError} instance will be returned with additional exception details.
     *
     * @param userId The unique user ID to remove the device for.
     * @param deviceGuid The unique ID of the device to remove the notifications for.
     */
    @AllowedForUser
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/unregister")
    public void unregister(@RequestParam("user-id") long userId, @RequestParam("device-guid") String deviceGuid) {
        mobileDeviceLinkService.unregister(userId, deviceGuid);
    }
}
