package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * VehicleNeedDto
 *
 * Purpose: DTO for vehicle-related needs (cars, bikes). Extends
 * `AbstractUserNeedDto` with vehicle-specific attributes to improve matching.
 *
 * Notable fields:
 * - `mandatoryFields` (List<String>): required seller-provided fields.
 * - `vehicleType` (VehicleType), `make`, `model`, `manufacturingYear`, `mileage`.
 * - `fuelType`, `color`, `vinNumber`.
 *
 * Frontend guidance:
 * - Use these fields to guide sellers in providing accurate offers and to
 *   populate vehicle search filters.
 */
public class VehicleNeedDto extends AbstractUserNeedDto {

    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();

	
	@JsonProperty("vehicle_type")
    private VehicleType vehicleType;
	
    private String make;
    private String model;
    
    @JsonProperty("manufacturing_year")
    private Integer manufacturingYear;
    
    private Double mileage;

    @JsonProperty("fuel_type")
    private String fuelType;

    private String color;

    @JsonProperty("vin_number")
    private String vinNumber;
}
