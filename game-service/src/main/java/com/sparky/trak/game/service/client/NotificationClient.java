package com.sparky.trak.game.service.client;

public interface NotificationClient {

    void send(long userId, String title, String content);
}
