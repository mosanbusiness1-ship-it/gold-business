package com.mo.core.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ProductIndexingEvent extends ApplicationEvent {
    private final String data;

    public ProductIndexingEvent(Object source, String data) {
        super(source);
        this.data = data;
    }
}