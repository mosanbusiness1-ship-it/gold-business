package com.mo.core.dtos;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

@Converter(autoApply = true)
/**
 * Data transfer object representing DurationToLongConverter in backend API integration.
 */
public class DurationToLongConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return (duration != null) ? duration.getSeconds() : null;
    }

    @Override
    public Duration convertToEntityAttribute(Long seconds) {
        return (seconds != null) ? Duration.ofSeconds(seconds) : null;
    }
}
