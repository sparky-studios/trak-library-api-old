package com.sparky.trak.notification.service;

public interface  NotificationService {

    void send(long userId, String title, String message);
}
