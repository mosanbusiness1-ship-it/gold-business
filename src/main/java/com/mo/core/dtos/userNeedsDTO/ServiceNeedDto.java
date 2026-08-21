package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.dtos.DurationToLongConverter;
import com.mo.core.enums.Currency;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.ProductType;

import jakarta.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * ServiceNeedDto
 *
 * Purpose: DTO for service-related needs (appointments, classes, on-demand
 * services). Extends `AbstractUserNeedDto` with availability, provider and
 * booking preferences.
 *
 * Notable fields:
 * - `autoPurchase` (boolean): whether the platform should auto-complete
 *   purchases when a matching service is available.
 * - `availableSlots` (List<LocalDateTime>): requested time slots.
 * - `serviceProvider`, `location`, `duration`, `onlineAvailable`.
 *
 * Frontend guidance:
 * - Present `availableSlots` in a calendar/time-picker and confirm availability
 *   before finalizing booking.
 */
public class ServiceNeedDto extends AbstractUserNeedDto {

    @JsonProperty("auto_purchase")
    private boolean autoPurchase;
    
    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();

    @JsonProperty("available_slots")
    private List<LocalDateTime> availableSlots;

    @JsonProperty("service_provider")
    private String serviceProvider;

    private String location;
    
    @JsonProperty("photo_urls")
    private List<String> photoUrls;

    @JsonIgnore
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonIgnore
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Boolean enabled;

    @JsonProperty("is_platform_owner")
    private Boolean isPlatformOwner;

    private Integer version;

    @Convert(converter = DurationToLongConverter.class)
    private Long duration;

    @JsonProperty("online_available")
    private Boolean onlineAvailable;

    @JsonProperty("available_after")
    private LocalDateTime availableAfter;

}