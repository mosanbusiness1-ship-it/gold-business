package com.mo.core.dtos.productsDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.Currency;
import com.mo.core.enums.ProductType;
import com.mo.core.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * VehicleProductDto
 *
 * Purpose: DTO for vehicle listings. Extends `AbstractProductDto` with vehicle
 * specific attributes used in listing, filtering and detail pages.
 *
 * Notable fields:
 * - `vehicleType` (VehicleType): classification (CAR, MOTORBIKE, TRUCK).
 * - `make`, `model`, `manufacturingYear`, `mileage`: key display attributes.
 * - `fuelType`, `color`, `vinNumber`: additional identifying metadata.
 *
 * Frontend guidance:
 * - Use `mileage` and `manufacturingYear` to compute quick condition indicators.
 * - Provide VIN display for verification and external vehicle checks.
 */
public class VehicleProductDto extends AbstractProductDto {

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