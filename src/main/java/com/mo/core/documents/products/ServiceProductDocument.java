package com.mo.core.documents.products;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.mo.core.enums.ProductType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/service-product.json")
@Document(indexName = "service_product_documents")
public class ServiceProductDocument extends AbstractProductDocument {

    @Field(type = FieldType.Keyword, name = "service_provider")
    private String serviceProvider;

    @Field(type = FieldType.Keyword, name = "location")
    private String location;

    @Field(type = FieldType.Long, name = "duration")
    private Long duration;

    @Field(type = FieldType.Boolean, name = "online_available")
    private Boolean onlineAvailable;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "available_after")
    private LocalDateTime availableAfter;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss", name = "available_slots")
    @Builder.Default
    private List<LocalDateTime> availableSlots = new ArrayList<>();

    @Field(type = FieldType.Double, name = "service_area_radius_km")
    private Double serviceAreaRadiusKm;

    @Field(type = FieldType.Keyword, name = "cancellation_policy")
    private String cancellationPolicy;

    @Field(type = FieldType.Keyword, name = "languages_spoken")
    private List<String> languagesSpoken;

    @Field(type = FieldType.Double, name = "provider_rating")
    private Double providerRating;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
