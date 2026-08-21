package com.mo.core.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base event class for organisation product validation workflow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganisationProductValidationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long organisationId;
    private Long productId;
    private Long moderatorId;
    private boolean approved;
    private String comments;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String eventType; // PENDING, APPROVED, REJECTED
    private Long slaMinutesElapsed; // time since submission
    private boolean slaExceeded; // true if > 24h (1440 min)
}
