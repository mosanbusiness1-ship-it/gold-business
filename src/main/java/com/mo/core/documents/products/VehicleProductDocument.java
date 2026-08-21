package com.mo.core.documents.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ProductType;
import com.mo.core.enums.VehicleType;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/vehicle-product.json")
@Document(indexName = "vehicle_products")
public class VehicleProductDocument extends AbstractProductDocument implements Serializable {

    @Field(type = FieldType.Keyword, name = "vehicle_type")
    private VehicleType vehicleType;

    @Field(type = FieldType.Keyword, name = "make")
    private String make;

    @Field(type = FieldType.Keyword, name = "model")
    private String model;

    @Field(type = FieldType.Integer, name = "manufacturing_year")
    private Integer manufacturingYear;

    @Field(type = FieldType.Double, name = "mileage")
    private Double mileage;

    @Field(type = FieldType.Keyword, name = "fuel_type")
    private String fuelType;

    @Field(type = FieldType.Keyword, name = "color")
    private String color;

    @Field(type = FieldType.Keyword, name = "vin_number")
    private String vinNumber;

    @Field(type = FieldType.Keyword, name = "transmission")
    private String transmission;

    @Field(type = FieldType.Keyword, name = "trim")
    private String trim;

    @Field(type = FieldType.Double, name = "fuel_consumption_l_per_100km")
    private Double fuelConsumptionLPer100km;

    @Field(type = FieldType.Integer, name = "doors")
    private Integer doors;

    @Field(type = FieldType.Keyword, name = "vehicle_condition")
    private String vehicleCondition;

    @Field(type = FieldType.Integer, name = "warranty_months")
    private Integer warrantyMonths;

    @Field(type = FieldType.Double, name = "seller_rating")
    private Double sellerRating;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}

