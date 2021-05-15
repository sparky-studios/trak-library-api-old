package com.sparkystudios.traklibrary.notification.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private long userId;

    private String title;

    private String content;
}
