package com.traklibrary.notification.service.listener;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.DeleteEndpointResult;
import com.traklibrary.notification.service.event.MobileDeviceLinkDeletedEvent;
import com.traklibrary.notification.service.exception.NotificationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class MobileDeviceLinkListenerTest {

    @Mock
    private AmazonSNS amazonSNS;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MobileDeviceLinkListener mobileDeviceLinkListener;

    @Test
    void onMobileDeviceLinkDeletedEvent_withException_throwsNotificationException() {
        // Arrange
        Mockito.when(amazonSNS.deleteEndpoint(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        MobileDeviceLinkDeletedEvent mobileDeviceLinkDeletedEvent = new MobileDeviceLinkDeletedEvent("endpoint-arn");

        // Assert
        Assertions.assertThatExceptionOfType(NotificationException.class)
                .isThrownBy(() -> mobileDeviceLinkListener.onMobileDeviceLinkDeletedEvent(mobileDeviceLinkDeletedEvent));
    }

    @Test
    void onMobileDeviceLinkDeletedEvent_withValidData_deletesEndpoint() {
        // Arrange
        Mockito.when(amazonSNS.deleteEndpoint(ArgumentMatchers.any()))
                .thenReturn(new DeleteEndpointResult());

        MobileDeviceLinkDeletedEvent mobileDeviceLinkDeletedEvent = new MobileDeviceLinkDeletedEvent("endpoint-arn");

        // Act
        mobileDeviceLinkListener.onMobileDeviceLinkDeletedEvent(mobileDeviceLinkDeletedEvent);

        // Assert
        Mockito.verify(amazonSNS)
                .deleteEndpoint(ArgumentMatchers.any());
    }
}
