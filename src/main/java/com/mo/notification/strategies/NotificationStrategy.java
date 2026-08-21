package com.mo.notification.strategies;

public interface NotificationStrategy<T> {
    void send(T payload);
    String channel(); // "EMAIL", "SMS", "PUSH"
    Class<T> getSupportedType();
}

