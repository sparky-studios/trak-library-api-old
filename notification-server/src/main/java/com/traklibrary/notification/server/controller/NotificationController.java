package com.traklibrary.notification.server.controller;

import com.traklibrary.notification.server.annotation.AllowedForAdmin;
import com.traklibrary.notification.server.annotation.AllowedForUser;
import com.traklibrary.notification.service.MobileDeviceLinkService;
import com.traklibrary.notification.service.NotificationService;
import com.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;
import com.traklibrary.notification.domain.MobileDeviceLink;
import com.traklibrary.notification.server.exception.ApiError;
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
 * @since 1.0.0
 * @author Sparky Studios
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/", produces = "application/vnd.traklibrary.v1.0+json")
public class NotificationController {

    private final MobileDeviceLinkService mobileDeviceLinkService;
    private final NotificationService notificationService;

    /**
     * End-point that will attempt to save a {@link MobileDeviceLink} with
     * the information provided in the {@link MobileDeviceLinkRegistrationRequestDto}. This endpoint will register
     * the information both within the persistence layer and the third-party push notification provider. It
     * should be noted that links can only be registered for users that have an ID matching that of the
     * currently authenticated user.
     *
     * The {@link MobileDeviceLinkRegistrationRequestDto} request body must contain valid information, otherwise
     * the end-point will return an {@link ApiError}. an
     * {@link ApiError} will also be returned if any issues occur
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
     * If any errors are thrown during removal, an {@link ApiError} instance
     * will be returned with additional exception details.
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

    /**
     * End-point that is used to dispatch push notifications to all devices that are currently registered against the
     * given user. The title and message provided <bold>must</bold> be UTF-8 encoded, otherwise it may lead to issues
     * invoking the end-point. It should be noted that push notifications can only be registered for users that have
     * an ID matching that of the currently authenticated user.
     *
     * If any errors are thrown during push notification sending, an {@link ApiError}
     * instance will be returned with additional exception details.
     *
     * @param userId The ID of the user to dispatch push notifications to.
     * @param title The title of the push notification message.
     * @param content The contents of the push notification message.
     */
    @AllowedForAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void send(@RequestParam("user-id") long userId,
                     @RequestParam String title,
                     @RequestParam String content) {
        notificationService.send(userId, title, content);
    }
}
