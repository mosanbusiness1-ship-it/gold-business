package com.mo.core.documents.needs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;

import com.mo.core.enums.RealEstateType;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "real_estate_need_ducuments")  // nom d'index en minuscules et snake_case
public class RealEstateNeedDocument extends AbstractUserNeedDocument {

    @Field(name = "address", type = FieldType.Text, analyzer = "standard")
    private String address;

    @Field(name = "city", type = FieldType.Keyword)
    private String city;

    @Field(name = "surface_area", type = FieldType.Double)
    private Double surfaceArea;

    @Field(name = "room_count", type = FieldType.Integer)
    private Integer roomCount;

    @Field(name = "bathroom_count", type = FieldType.Integer)
    private Integer bathroomCount;

    @Field(name = "real_estate_type", type = FieldType.Keyword)
    private RealEstateType realEstateType;

    @Field(name = "is_for_rent", type = FieldType.Boolean)
    private Boolean isForRent;

    @Field(name = "is_for_sale", type = FieldType.Boolean)
    private Boolean isForSale;

    @Field(name = "construction_year", type = FieldType.Integer)
    private Integer constructionYear;

    @Field(name = "energy_class", type = FieldType.Keyword)
    private String energyClass;

    @Field(name = "move_in_date", type = FieldType.Date, format = DateFormat.date_time)
    private java.time.LocalDate moveInDate;

    @Field(name = "max_hoa_fee", type = FieldType.Double)
    private BigDecimal maxHOAFee;

    @Field(name = "min_bedrooms", type = FieldType.Integer)
    private Integer minBedrooms;

    @Field(name = "preferred_neighborhoods", type = FieldType.Keyword)
    private java.util.List<String> preferredNeighborhoods;

    @Field(name = "school_district", type = FieldType.Keyword)
    private String schoolDistrict;

    @Field(name = "pet_friendly", type = FieldType.Boolean)
    private Boolean petFriendly;

    @Field(name = "max_price", type = FieldType.Double)
    private BigDecimal maxPrice;

    @Field(name = "all_text", type = FieldType.Text, analyzer = "standard")
    private String allText;
}

