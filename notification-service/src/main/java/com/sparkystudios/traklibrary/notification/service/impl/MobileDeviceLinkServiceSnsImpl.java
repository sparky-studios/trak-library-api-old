package com.sparkystudios.traklibrary.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.google.common.base.Strings;
import com.sparkystudios.traklibrary.notification.domain.MobileDeviceLink;
import com.sparkystudios.traklibrary.notification.repository.MobileDeviceLinkRepository;
import com.sparkystudios.traklibrary.notification.service.MobileDeviceLinkService;
import com.sparkystudios.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;
import com.sparkystudios.traklibrary.notification.service.event.MobileDeviceLinkDeletedEvent;
import com.sparkystudios.traklibrary.notification.service.exception.NotificationException;
import com.sparkystudios.traklibrary.security.AuthenticationService;
import com.sparkystudios.traklibrary.security.exception.InvalidUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MobileDeviceLinkServiceSnsImpl implements MobileDeviceLinkService {

    private static final String INVALID_USER_MESSAGE = "notifications.exception.invalid-user";
    private static final String REGISTRATION_FAILED_MESSAGE = "notifications.exception.registration-failed";

    @Value("${trak.aws.simple-notification-service.android-arn}")
    private String androidArn;

    private final AmazonSNS amazonSNS;
    private final AuthenticationService authenticationService;
    private final MobileDeviceLinkRepository mobileDeviceLinkRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessageSource messageSource;

    @Override
    public void register(MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto) {
        // Ensure the authenticated user is the one being registered.
        if (!authenticationService.isCurrentAuthenticatedUser(mobileDeviceLinkRegistrationRequestDto.getUserId())) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        // See if we have any device links already registered for the given device.
        Optional<MobileDeviceLink> existingDeviceLink = mobileDeviceLinkRepository.findByDeviceGuid(mobileDeviceLinkRegistrationRequestDto.getDeviceGuid());
        String endpointArn = existingDeviceLink.isPresent() ? existingDeviceLink.get().getEndpointArn() : "";

        if (Strings.isNullOrEmpty(endpointArn)) {
            endpointArn = createEndpoint(mobileDeviceLinkRegistrationRequestDto);
        }

        try {
            GetEndpointAttributesRequest request = new GetEndpointAttributesRequest()
                    .withEndpointArn(endpointArn);

            GetEndpointAttributesResult result = amazonSNS.getEndpointAttributes(request);

            boolean updateNeeded = !result.getAttributes().get("Token").equals(mobileDeviceLinkRegistrationRequestDto.getToken()) ||
                    !"true".equalsIgnoreCase(result.getAttributes().get("Enabled"));

            if (updateNeeded) {
                Map<String, String> attributes = new HashMap<>();
                attributes.put("Token", mobileDeviceLinkRegistrationRequestDto.getToken());
                attributes.put("Enabled", Boolean.TRUE.toString());

                SetEndpointAttributesRequest attributesRequest = new SetEndpointAttributesRequest()
                        .withEndpointArn(endpointArn)
                        .withAttributes(attributes);

                amazonSNS.setEndpointAttributes(attributesRequest);
            }
        } catch (NotFoundException nfe) {
            log.info("Changes found. Creating new AWS endpoint.");
            endpointArn = createEndpoint(mobileDeviceLinkRegistrationRequestDto);
        } catch (@SuppressWarnings({"squid:S2221"}) Exception e) {
            log.error("Failed to retrieve AWS endpoint attributes.", e);

            String errorMessage =
                    messageSource.getMessage(REGISTRATION_FAILED_MESSAGE, new Object[]{}, LocaleContextHolder.getLocale());

            throw new NotificationException(errorMessage, e);
        }

        // Once the information has been generated in AWS, we need to keep a reference so that push
        // notifications can be dispatched to the devices.
        MobileDeviceLink mobileDeviceLink = existingDeviceLink.orElse(new MobileDeviceLink());
        mobileDeviceLink.setUserId(mobileDeviceLinkRegistrationRequestDto.getUserId());
        mobileDeviceLink.setDeviceGuid(mobileDeviceLinkRegistrationRequestDto.getDeviceGuid());
        mobileDeviceLink.setToken(mobileDeviceLinkRegistrationRequestDto.getToken());
        mobileDeviceLink.setEndpointArn(endpointArn);
        mobileDeviceLink.setLinkedDate(LocalDateTime.now());

        mobileDeviceLinkRepository.save(mobileDeviceLink);
    }

    private String createEndpoint(MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto) {
        CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest()
                .withCustomUserData(mobileDeviceLinkRegistrationRequestDto.getDeviceGuid())
                .withPlatformApplicationArn(androidArn)
                .withToken(mobileDeviceLinkRegistrationRequestDto.getToken());

        return amazonSNS.createPlatformEndpoint(request).getEndpointArn();
    }

    @Override
    public void unregister(long userId, String deviceGuid) {
        // Ensure the authenticated user is the one being unregistered.
        if (!authenticationService.isCurrentAuthenticatedUser(userId)) {
            String errorMessage = messageSource
                    .getMessage(INVALID_USER_MESSAGE, new Object[] {}, LocaleContextHolder.getLocale());

            throw new InvalidUserException(errorMessage);
        }

        // Check to see if there is any mobile device link that matches the given criteria, if not throw
        // an exception.
        MobileDeviceLink mobileDeviceLink = mobileDeviceLinkRepository.findByUserIdAndDeviceGuid(userId, deviceGuid)
                .orElseThrow(() -> new EntityNotFoundException(""));

        // We'll want to delete the device link, regardless of whether it failed deleting in AWS.
        mobileDeviceLinkRepository.delete(mobileDeviceLink);

        // Publish the AWS endpoint deletion as an event, we don't want this called within a transaction.
        applicationEventPublisher
                .publishEvent(new MobileDeviceLinkDeletedEvent(mobileDeviceLink.getEndpointArn()));
    }
}
