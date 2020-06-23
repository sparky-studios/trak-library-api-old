package com.sparky.trak.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.sparky.trak.notification.domain.MobileDeviceLink;
import com.sparky.trak.notification.repository.MobileDeviceLinkRepository;
import com.sparky.trak.notification.service.NotificationService;
import com.sparky.trak.notification.service.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class NotificationServiceSnsImpl implements NotificationService {

    private final MobileDeviceLinkRepository mobileDeviceLinkRepository;
    private final AmazonSNS amazonSNS;
    private final MessageSource messageSource;

    @Override
    public void send(long userId, String title, String message) {
        // Retrieve all of the linked devices for the specified user.
        Collection<MobileDeviceLink> mobileDeviceLinks = mobileDeviceLinkRepository
                .findAllByUserId(userId);

        for (MobileDeviceLink mobileDeviceLink : mobileDeviceLinks) {
            // Create the push notification.
            PublishRequest publishRequest = new PublishRequest()
                    .withSubject(title)
                    .withMessage(message)
                    .withTargetArn(mobileDeviceLink.getEndpointArn());

            // Publish the request through AWS.
            try {
                amazonSNS.publish(publishRequest);
            } catch (Exception e) {
                String errorMessage =
                        messageSource.getMessage("notifications.exception.publish-failed", new Object[] {mobileDeviceLink.getToken()}, LocaleContextHolder.getLocale());

                throw new NotificationException(errorMessage, e);
            }
        }
    }
}
