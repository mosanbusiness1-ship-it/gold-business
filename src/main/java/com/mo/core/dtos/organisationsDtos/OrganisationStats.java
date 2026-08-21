package com.mo.core.dtos.organisationsDtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * OrganisationStats
 *
 * Purpose: DTO returning aggregated statistics for an organisation.
 * Used on dashboards to show activity and growth metrics.
 *
 * Key fields:
 * - `totalProducts` (long): count of all products in the org.
 * - `activeMembers` (long): number of active members.
 * - `childOrganisations` (long): count of sub-orgs (if hierarchical).
 * - `lastActivityDate` (LocalDateTime): most recent action timestamp.
 *
 * Frontend guidance:
 * - Display these metrics on org dashboard/overview pages.
 * - Use `isActive()` method to show activity status visually (green if active).
 */
public class OrganisationStats {
    private long totalProducts;
    private long activeMembers;
    private long childOrganisations;
//    private int activeProducts;
    private LocalDateTime lastActivityDate;
    
    // Méthode calculée
    public boolean isActive() {
        return lastActivityDate.isAfter(LocalDateTime.now().minusMonths(1));
    }
}