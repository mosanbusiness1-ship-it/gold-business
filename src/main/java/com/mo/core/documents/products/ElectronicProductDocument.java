package com.mo.core.documents.products;

import com.mo.core.enums.ElectronicType;
import com.mo.core.enums.ProductType;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/electronic-product.json")
@Document(indexName = "electronic_product_documents")
public class ElectronicProductDocument extends AbstractProductDocument {

    @Field(type = FieldType.Keyword, name = "electronic_type")
    private ElectronicType electronicType;

    @Field(type = FieldType.Keyword, analyzer = "standard", name = "brand")
    private String brand;

    @Field(type = FieldType.Keyword, name = "model")
    private String model;

    @Field(type = FieldType.Text, name = "specifications")
    private String specifications;

    @Field(type = FieldType.Keyword, name = "warranty_period")
    private String warrantyPeriod;

    @Field(type = FieldType.Integer, name = "release_year")
    private Integer releaseYear;

    @Field(type = FieldType.Integer, name = "battery_health_percent")
    private Integer batteryHealthPercent;

    @Field(type = FieldType.Keyword, name = "accessories_included")
    private List<String> accessoriesIncluded;

    @Field(type = FieldType.Keyword, name = "supported_networks")
    private List<String> supportedNetworks;

    @Field(type = FieldType.Integer, name = "warranty_months")
    private Integer warrantyMonths;

    @Field(type = FieldType.Double, name = "seller_rating")
    private Double sellerRating;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
