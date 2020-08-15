package com.traklibrary.notification.service.impl;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesResult;
import com.amazonaws.services.sns.model.NotFoundException;
import com.traklibrary.notification.domain.MobileDeviceLink;
import com.traklibrary.notification.repository.MobileDeviceLinkRepository;
import com.traklibrary.notification.service.AuthenticationService;
import com.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;
import com.traklibrary.notification.service.event.MobileDeviceLinkDeletedEvent;
import com.traklibrary.notification.service.exception.InvalidUserException;
import com.traklibrary.notification.service.exception.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MobileDeviceLinkServiceSnsImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private MobileDeviceLinkRepository mobileDeviceLinkRepository;

    @Mock
    private AmazonSNS amazonSNS;

    @Mock
    private MessageSource messageSource;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private MobileDeviceLinkServiceSnsImpl mobileDeviceLinkService;

    @Test
    public void register_withIncorrectUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(InvalidUserException.class,
                () -> mobileDeviceLinkService.register(new MobileDeviceLinkRegistrationRequestDto()));
    }

    @Test
    public void register_withNonExistentMobileDeviceLink_createsEndpointAndMobileDeviceLink() {
        // Arrange
        MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto = new MobileDeviceLinkRegistrationRequestDto();
        mobileDeviceLinkRegistrationRequestDto.setToken("token");
        mobileDeviceLinkRegistrationRequestDto.setDeviceGuid("device-guid");

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByDeviceGuid(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        CreatePlatformEndpointResult createPlatformEndpointResult = new CreatePlatformEndpointResult()
                .withEndpointArn("endpoint-arn");

        Mockito.when(amazonSNS.createPlatformEndpoint(ArgumentMatchers.any()))
            .thenReturn(createPlatformEndpointResult);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("Token", mobileDeviceLinkRegistrationRequestDto.getToken());
        attributes.put("Enabled", Boolean.TRUE.toString());

        GetEndpointAttributesResult getEndpointAttributesResult = new GetEndpointAttributesResult()
                .withAttributes(attributes);

        Mockito.when(amazonSNS.getEndpointAttributes(ArgumentMatchers.any()))
                .thenReturn(getEndpointAttributesResult);

        Mockito.when(mobileDeviceLinkRepository.save(ArgumentMatchers.any()))
                .thenReturn(null);

        // Act
        mobileDeviceLinkService.register(mobileDeviceLinkRegistrationRequestDto);

        // Assert
        Mockito.verify(amazonSNS, Mockito.atMostOnce())
                .createPlatformEndpoint(ArgumentMatchers.any());

        Mockito.verify(amazonSNS, Mockito.never())
                .setEndpointAttributes(ArgumentMatchers.any());

        Mockito.verify(mobileDeviceLinkRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void register_withEndpointAttributesThatNeedUpdating_invokesSetEndpointAttributes() {
        // Arrange
        MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto = new MobileDeviceLinkRegistrationRequestDto();
        mobileDeviceLinkRegistrationRequestDto.setToken("token");
        mobileDeviceLinkRegistrationRequestDto.setDeviceGuid("device-guid");

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByDeviceGuid(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        CreatePlatformEndpointResult createPlatformEndpointResult = new CreatePlatformEndpointResult()
                .withEndpointArn("endpoint-arn");

        Mockito.when(amazonSNS.createPlatformEndpoint(ArgumentMatchers.any()))
                .thenReturn(createPlatformEndpointResult);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("Token", "token-1");
        attributes.put("Enabled", Boolean.TRUE.toString());

        GetEndpointAttributesResult getEndpointAttributesResult = new GetEndpointAttributesResult()
                .withAttributes(attributes);

        Mockito.when(amazonSNS.getEndpointAttributes(ArgumentMatchers.any()))
                .thenReturn(getEndpointAttributesResult);

        Mockito.when(mobileDeviceLinkRepository.save(ArgumentMatchers.any()))
                .thenReturn(null);

        // Act
        mobileDeviceLinkService.register(mobileDeviceLinkRegistrationRequestDto);

        // Assert
        Mockito.verify(amazonSNS, Mockito.atMostOnce())
                .createPlatformEndpoint(ArgumentMatchers.any());

        Mockito.verify(amazonSNS, Mockito.atMostOnce())
                .setEndpointAttributes(ArgumentMatchers.any());

        Mockito.verify(mobileDeviceLinkRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void register_withNotFoundException_recreatedEndpointArn() {
        // Arrange
        MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto = new MobileDeviceLinkRegistrationRequestDto();
        mobileDeviceLinkRegistrationRequestDto.setToken("token");
        mobileDeviceLinkRegistrationRequestDto.setDeviceGuid("device-guid");

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByDeviceGuid(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        CreatePlatformEndpointResult createPlatformEndpointResult = new CreatePlatformEndpointResult()
                .withEndpointArn("endpoint-arn");

        Mockito.when(amazonSNS.createPlatformEndpoint(ArgumentMatchers.any()))
                .thenReturn(createPlatformEndpointResult);

        Mockito.when(amazonSNS.getEndpointAttributes(ArgumentMatchers.any()))
                .thenThrow(new NotFoundException(""));

        Mockito.when(mobileDeviceLinkRepository.save(ArgumentMatchers.any()))
                .thenReturn(null);

        // Act
        mobileDeviceLinkService.register(mobileDeviceLinkRegistrationRequestDto);

        // Assert
        Mockito.verify(amazonSNS, Mockito.atMost(2))
                .createPlatformEndpoint(ArgumentMatchers.any());

        Mockito.verify(amazonSNS, Mockito.atMostOnce())
                .setEndpointAttributes(ArgumentMatchers.any());

        Mockito.verify(mobileDeviceLinkRepository, Mockito.atMostOnce())
                .save(ArgumentMatchers.any());
    }

    @Test
    public void register_withGenericException_throwsNotificationException() {
        // Arrange
        MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto = new MobileDeviceLinkRegistrationRequestDto();
        mobileDeviceLinkRegistrationRequestDto.setToken("token");
        mobileDeviceLinkRegistrationRequestDto.setDeviceGuid("device-guid");

        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByDeviceGuid(ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        CreatePlatformEndpointResult createPlatformEndpointResult = new CreatePlatformEndpointResult()
                .withEndpointArn("endpoint-arn");

        Mockito.when(amazonSNS.createPlatformEndpoint(ArgumentMatchers.any()))
                .thenReturn(createPlatformEndpointResult);

        Mockito.when(amazonSNS.getEndpointAttributes(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException());

        Mockito.when(messageSource.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(Locale.class)))
                .thenReturn("");

        // Assert
        Assertions.assertThrows(NotificationException.class,
                () -> mobileDeviceLinkService.register(mobileDeviceLinkRegistrationRequestDto));
    }

    @Test
    public void unregister_withIncorrectUser_throwsInvalidUserException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(false);

        // Assert
        Assertions.assertThrows(InvalidUserException.class,
                () -> mobileDeviceLinkService.unregister(0L, ""));
    }

    @Test
    public void unregister_withNoMobileDeviceLink_throwsEntityNotFoundException() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByUserIdAndDeviceGuid(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> mobileDeviceLinkService.unregister(0L , ""));
    }

    @Test
    public void unregister_withMobileDeviceLink_deletesAndInvokesEvent() {
        // Arrange
        Mockito.when(authenticationService.isCurrentAuthenticatedUser(ArgumentMatchers.anyLong()))
                .thenReturn(true);

        Mockito.when(mobileDeviceLinkRepository.findByUserIdAndDeviceGuid(ArgumentMatchers.anyLong(), ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(new MobileDeviceLink()));

        Mockito.doNothing().when(mobileDeviceLinkRepository)
                .delete(ArgumentMatchers.any());

        Mockito.doNothing().when(applicationEventPublisher)
                .publishEvent(ArgumentMatchers.any(MobileDeviceLinkDeletedEvent.class));

        // Act
        mobileDeviceLinkService.unregister(0L, "");

        // Assert
        Mockito.verify(mobileDeviceLinkRepository, Mockito.atMostOnce())
                .delete(ArgumentMatchers.any());

        Mockito.verify(applicationEventPublisher, Mockito.atMostOnce())
                .publishEvent(ArgumentMatchers.any(MobileDeviceLinkDeletedEvent.class));
    }
}
