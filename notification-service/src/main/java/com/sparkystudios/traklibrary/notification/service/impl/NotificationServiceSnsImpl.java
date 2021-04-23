package com.sparkystudios.traklibrary.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.sparkystudios.traklibrary.notification.domain.MobileDeviceLink;
import com.sparkystudios.traklibrary.notification.repository.MobileDeviceLinkRepository;
import com.sparkystudios.traklibrary.notification.service.NotificationService;
import com.sparkystudios.traklibrary.notification.service.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class NotificationServiceSnsImpl implements NotificationService {

    private static final String PUBLISH_FAILED_MESSAGE = "notifications.exception.publish-failed";

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
            var publishRequest = new PublishRequest()
                    .withSubject(title)
                    .withMessage(message)
                    .withTargetArn(mobileDeviceLink.getEndpointArn());

            // Publish the request through AWS.
            try {
                amazonSNS.publish(publishRequest);
            } catch (Exception e) {
                String errorMessage =
                        messageSource.getMessage(PUBLISH_FAILED_MESSAGE, new Object[] {mobileDeviceLink.getToken()}, LocaleContextHolder.getLocale());

                throw new NotificationException(errorMessage, e);
            }
        }
    }
}
