package com.mo.core.model.needs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.VehicleType;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
//@DiscriminatorValue("VEHICLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public
class VehicleNeed extends AbstractUserNeed {

    @Enumerated(EnumType.STRING)
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
    
    @JsonProperty("max_mileage")
    private Double maxMileage;
    
    @JsonProperty("preferred_transmission")
    private String preferredTransmission;
    
    @JsonProperty("min_year")
    private Integer minYear;
    
    @JsonProperty("vehicle_condition_preferred")
    private String vehicleConditionPreferred;
    
    @JsonProperty("location_radius_km")
    private Double locationRadiusKm;
    
    @JsonProperty("accept_imported")
    private Boolean acceptImported = true;

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
