package com.mo.core.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * CreateRoleRequest
 *
 * Purpose: request DTO for creating a new role in the system.
 *
 * Fields:
 * - `name` (String): the name of the role to create.
 *
 * Frontend guidance:
 * - Use this DTO in admin or permissions management flows.
 * - Validate the role name is unique before submission if possible.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateRoleRequest {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
