package com.traklibrary.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.traklibrary.notification.domain.MobileDeviceLink;
import com.traklibrary.notification.repository.MobileDeviceLinkRepository;
import com.traklibrary.notification.service.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class NotificationServiceSnsImplTest {

    @Mock
    private MobileDeviceLinkRepository mobileDeviceLinkRepository;

    @Mock
    private AmazonSNS amazonSNS;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private NotificationServiceSnsImpl notificationService;

    @Test
    void send_withEmptyMobileDeviceLinks_doesntPublishNotifications() {
        // Arrange
        Mockito.when(mobileDeviceLinkRepository.findAllByUserId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.emptyList());

        // Act
        notificationService.send(0L, "test-title", "test-message");

        // Assert
        Mockito.verify(amazonSNS, Mockito.never())
            .publish(ArgumentMatchers.any());
    }

    @Test
    void send_withErroneousPublishRequest_throwsNotificationException() {
        // Arrange
        MobileDeviceLink mobileDeviceLink = new MobileDeviceLink();
        mobileDeviceLink.setEndpointArn("endpoint-arn");

        Mockito.when(mobileDeviceLinkRepository.findAllByUserId(ArgumentMatchers.anyLong()))
                .thenReturn(Collections.singletonList(mobileDeviceLink));

        Mockito.when(amazonSNS.publish(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(NotificationException.class,
                () -> notificationService.send(0L, "test-title", "test-message"));
    }

    @Test
    void send_withValidMobileDeviceLinks_invokesPublish() {
        // Arrange
        MobileDeviceLink mobileDeviceLink1 = new MobileDeviceLink();
        mobileDeviceLink1.setEndpointArn("endpoint-arn-1");

        MobileDeviceLink mobileDeviceLink2 = new MobileDeviceLink();
        mobileDeviceLink2.setEndpointArn("endpoint-arn-2");

        Mockito.when(mobileDeviceLinkRepository.findAllByUserId(ArgumentMatchers.anyLong()))
                .thenReturn(Arrays.asList(mobileDeviceLink1, mobileDeviceLink2));

        Mockito.when(amazonSNS.publish(ArgumentMatchers.any()))
            .thenReturn(null);

        // Act
        notificationService.send(0L, "test-title", "test-message");

        // Assert
        Mockito.verify(amazonSNS, Mockito.atMost(2))
                .publish(ArgumentMatchers.any());
    }
}
