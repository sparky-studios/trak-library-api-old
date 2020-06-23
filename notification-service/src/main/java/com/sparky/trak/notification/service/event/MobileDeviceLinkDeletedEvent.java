package com.sparky.trak.notification.service.event;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class MobileDeviceLinkDeletedEvent {

    private final String endpointArn;
}
