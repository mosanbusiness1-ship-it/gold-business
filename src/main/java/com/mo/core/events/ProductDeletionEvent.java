package com.mo.core.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class ProductDeletionEvent extends ApplicationEvent {
    private final Long productId;

    public ProductDeletionEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }
}
