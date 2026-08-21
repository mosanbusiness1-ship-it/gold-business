package com.mo.core.documents.products;

import com.mo.core.enums.ProductType;
import com.mo.core.enums.RealEstateType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/real-estate-product.json")
@Document(indexName = "real_estate_product_documents")
public class RealEstateProductDocument extends AbstractProductDocument {

    @Field(type = FieldType.Text, analyzer = "standard", name = "address")
    private String address;

    @Field(type = FieldType.Keyword, name = "city")
    private String city;

    @Field(type = FieldType.Double, name = "surface_area")
    private Double surfaceArea;

    @Field(type = FieldType.Integer, name = "room_count")
    private Integer roomCount;

    @Field(type = FieldType.Integer, name = "bathroom_count")
    private Integer bathroomCount;

    @Field(type = FieldType.Keyword, name = "real_estate_type")
    private RealEstateType realEstateType;

    @Field(type = FieldType.Boolean, name = "is_for_rent")
    private Boolean isForRent;

    @Field(type = FieldType.Boolean, name = "is_for_sale")
    private Boolean isForSale;

    @Field(type = FieldType.Integer, name = "construction_year")
    private Integer constructionYear;

    @Field(type = FieldType.Keyword, name = "energy_class")
    private String energyClass;

    @Field(type = FieldType.Integer, name = "floor")
    private Integer floor;

    @Field(type = FieldType.Boolean, name = "balcony")
    private Boolean balcony;

    @Field(type = FieldType.Boolean, name = "furnished")
    private Boolean furnished;

    @Field(type = FieldType.Double, name = "hoa_fees")
    private BigDecimal hoaFees;

    @Field(type = FieldType.Keyword, name = "parking")
    private String parking;

    @Field(type = FieldType.Integer, name = "energy_rating_numeric")
    private Integer energyRatingNumeric;

    @Field(type = FieldType.Keyword, name = "neighborhood_tags")
    private List<String> neighborhoodTags;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
