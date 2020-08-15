package com.traklibrary.notification.service;

import com.traklibrary.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;

public interface MobileDeviceLinkService {

    void register(MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto);

    void unregister(long userId, String deviceGuid);
}
