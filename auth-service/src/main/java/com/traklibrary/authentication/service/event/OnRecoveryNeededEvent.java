package com.traklibrary.authentication.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class OnRecoveryNeededEvent extends ApplicationEvent {

    @Getter
    private final String emailAddress;

    @Getter
    private final String username;

    public OnRecoveryNeededEvent(Object source, String emailAddress, String username) {
        super(source);
        this.emailAddress = emailAddress;
        this.username = username;
    }
}
