package com.mo.core.documents.needs;

import lombok.*;
import lombok.experimental.SuperBuilder;

import org.springframework.data.elasticsearch.annotations.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.VehicleType;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "vehicle_need_documents")  // même index mais distingué par le champ "type"
public class VehicleNeedDocument extends AbstractUserNeedDocument {

    @Field(type = FieldType.Keyword, name = "vehicle_type")
    @JsonProperty("vehicle_type")
    private VehicleType vehicleType;

    @Field(type = FieldType.Keyword, name = "make")
    private String make;

    @Field(type = FieldType.Keyword, name = "model")
    private String model;

    @Field(type = FieldType.Integer, name = "manufacturing_year")
    @JsonProperty("manufacturing_year")
    private Integer manufacturingYear;

    @Field(type = FieldType.Double, name = "mileage")
    private Double mileage;

    @Field(type = FieldType.Keyword, name = "fuel_type")
    @JsonProperty("fuel_type")
    private String fuelType;

    @Field(type = FieldType.Keyword, name = "color")
    private String color;

    @Field(type = FieldType.Keyword, name = "vin_number")
    @JsonProperty("vin_number")
    private String vinNumber;

    @Field(type = FieldType.Double, name = "max_mileage")
    private Double maxMileage;

    @Field(type = FieldType.Keyword, name = "preferred_transmission")
    private String preferredTransmission;

    @Field(type = FieldType.Integer, name = "min_year")
    private Integer minYear;

    @Field(type = FieldType.Keyword, name = "vehicle_condition_preferred")
    private String vehicleConditionPreferred;

    @Field(type = FieldType.Double, name = "location_radius_km")
    private Double locationRadiusKm;

    @Field(type = FieldType.Boolean, name = "accept_imported")
    private Boolean acceptImported;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}

