package com.sparkystudios.traklibrary.authentication.service.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class OnVerificationNeededEvent extends ApplicationEvent {

    @Getter
    private final String username;

    @Getter
    private final String emailAddress;

    public OnVerificationNeededEvent(Object source, String username, String emailAddress) {
        super(source);
        this.username = username;
        this.emailAddress = emailAddress;
    }
}
