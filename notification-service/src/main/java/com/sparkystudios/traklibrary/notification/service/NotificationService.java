package com.sparkystudios.traklibrary.notification.service;

public interface  NotificationService {

    void send(long userId, String title, String message);
}
