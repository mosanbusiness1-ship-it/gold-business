package com.mo.notification.events;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Object data;

    public NotificationEvent(Object source, Object data) {
        super(source);
        this.data = data;
    }
}
