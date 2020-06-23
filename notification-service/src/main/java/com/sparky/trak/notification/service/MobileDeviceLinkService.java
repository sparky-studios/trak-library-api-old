package com.sparky.trak.notification.service;

import com.sparky.trak.notification.service.dto.MobileDeviceLinkRegistrationRequestDto;

public interface MobileDeviceLinkService {

    void register(MobileDeviceLinkRegistrationRequestDto mobileDeviceLinkRegistrationRequestDto);

    void unregister(long userId, String deviceGuid);
}
