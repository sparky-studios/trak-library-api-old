package com.sparkystudios.traklibrary.notification.service.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class MobileDeviceLinkRegistrationRequestDto {

    private long userId;

    @Size(min = 36, max = 36, message = "{mobile-device-link-registration-request.validation.device-guid.size}")
    @NotEmpty(message = "{mobile-device-link-registration-request.validation.device-guid.not-empty}")
    private String deviceGuid;

    @Size(max = 255, message = "{mobile-device-link-registration-request.validation.token.size}")
    @NotEmpty(message = "{mobile-device-link-registration-request.validation.token.not-empty}")
    private String token;
}
