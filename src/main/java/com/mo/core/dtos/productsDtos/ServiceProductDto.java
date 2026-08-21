package com.mo.core.dtos.productsDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.dtos.DurationToLongConverter;
import com.mo.core.enums.Currency;
import com.mo.core.enums.ProductType;

import jakarta.persistence.Convert;

@Data
@NoArgsConstructor
/**
 * ServiceProductDto
 *
 * Purpose: DTO for service-type products (appointments, consultations,
 * classes). Provides availability, provider and duration metadata.
 *
 * Notable fields:
 * - `availableSlots` (List<LocalDateTime>): times the service is available.
 * - `serviceProvider` (String): name/id of provider to display.
 * - `duration` (Long): service duration in seconds (stored via converter).
 * - `onlineAvailable` / `availableAfter`: availability control fields.
 *
 * Frontend guidance:
 * - Present `availableSlots` in a time-picker or calendar view.
 * - When booking, send the chosen slot and confirm against backend availability.
 */
public class ServiceProductDto extends AbstractProductDto {

    @JsonProperty("available_slots")
    private List<LocalDateTime> availableSlots;

    @JsonProperty("service_provider")
    private String serviceProvider;

    private String location;

    @Convert(converter = DurationToLongConverter.class)
    private Long duration;

    @JsonProperty("online_available")
    private Boolean onlineAvailable;

    @JsonProperty("available_after")
    private LocalDateTime availableAfter;

}