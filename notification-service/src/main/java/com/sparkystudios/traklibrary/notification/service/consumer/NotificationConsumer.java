package com.sparkystudios.traklibrary.notification.service.consumer;

import com.sparkystudios.traklibrary.notification.service.NotificationService;
import com.sparkystudios.traklibrary.notification.service.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    /**
     * {@link Consumer} registered with Spring Cloud Stream that responds to any published
     * "trak-notification-send" events. Its' purpose is to merely retrieve the values from the
     * {@link NotificationEvent} sent from the publisher, and pass on the data to the
     * {@link NotificationService}.
     *
     * @return The {@link Consumer} that consumes the Spring Cloud Stream event.
     */
    @Bean
    public Consumer<NotificationEvent> sendNotification() {
        return notificationEvent ->
                notificationService.send(notificationEvent.getUserId(), notificationEvent.getTitle(), notificationEvent.getContent());
    }
}
