package com.sparkystudios.traklibrary.game.service.client;

public interface NotificationClient {

    void send(long userId, String title, String content);
}
