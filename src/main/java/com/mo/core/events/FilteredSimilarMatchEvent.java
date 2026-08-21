package com.mo.core.events;

import org.springframework.context.ApplicationEvent;

import com.mo.core.dtos.ProductAndMatchedNeedsDTO;

import lombok.Getter;

@Getter
public class FilteredSimilarMatchEvent extends ApplicationEvent {
    private final ProductAndMatchedNeedsDTO data;

    public FilteredSimilarMatchEvent(Object source, ProductAndMatchedNeedsDTO data) {
        super(source);
        this.data = data;
    }
}
