package com.sparkystudios.traklibrary.notification.service.listener;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.sparkystudios.traklibrary.notification.service.event.MobileDeviceLinkDeletedEvent;
import com.sparkystudios.traklibrary.notification.service.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class MobileDeviceLinkListener {

    private final AmazonSNS amazonSNS;
    private final MessageSource messageSource;

    @TransactionalEventListener
    public void onMobileDeviceLinkDeletedEvent(MobileDeviceLinkDeletedEvent event) {
        log.info("Deleting AWS endpoint: " + event.getEndpointArn());

        try {
            var deleteEndpointRequest = new DeleteEndpointRequest()
                    .withEndpointArn(event.getEndpointArn());

            amazonSNS.deleteEndpoint(deleteEndpointRequest);
        } catch (Exception e) {
            String errorMessage =
                    messageSource.getMessage("notifications.exception.delete-failed", new Object[]{}, LocaleContextHolder.getLocale());

            throw new NotificationException(errorMessage, e);
        }
    }
}
