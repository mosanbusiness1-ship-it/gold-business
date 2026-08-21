package com.mo.core.documents.needs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.mo.core.enums.FoodCategory;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "food_need_documents")  // même index que les autres besoins
public class FoodNeedDocument extends AbstractUserNeedDocument {

    @Field(name = "food_category", type = FieldType.Keyword)
    private FoodCategory foodCategory;

    @Field(name = "expiry_date", type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime expiryDate;

    @Field(name = "nutritional_info", type = FieldType.Text, analyzer = "standard")
    private String nutritionalInfo;

    @Field(name = "organic", type = FieldType.Boolean)
    private Boolean organic;

    @Field(name = "gluten_free", type = FieldType.Boolean)
    private Boolean glutenFree;

    @Field(name = "weight", type = FieldType.Double)
    private Double weight;

    @Field(name = "dietary_restrictions", type = FieldType.Keyword)
    private java.util.List<String> dietaryRestrictions;

    @Field(name = "min_shelf_life_days", type = FieldType.Integer)
    private Integer minShelfLifeDays;

    @Field(name = "preferred_origin", type = FieldType.Keyword)
    private String preferredOrigin;

    @Field(name = "delivery_temperature_required", type = FieldType.Keyword)
    private String deliveryTemperatureRequired;

    @Field(name = "all_text", type = FieldType.Text, analyzer = "standard")
    private String allText;
}
