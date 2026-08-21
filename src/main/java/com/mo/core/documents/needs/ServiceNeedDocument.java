package com.mo.core.documents.needs;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Document(indexName = "service_need_documents") // même index que les autres besoins
public class ServiceNeedDocument extends AbstractUserNeedDocument {

    @Field(name = "service_provider", type = FieldType.Keyword)
    private String serviceProvider;

    @Field(name = "location", type = FieldType.Keyword)
    private String location;

    @Field(name = "duration", type = FieldType.Long)
    private Long duration;

    @Field(name = "available_after", type = FieldType.Date, format = DateFormat.date_time)
    @JsonProperty("available_after")
    private LocalDateTime availableAfter;

    @Field(name = "online_available", type = FieldType.Boolean)
    private Boolean onlineAvailable;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss", name = "available_slots")
    @Builder.Default
    private List<LocalDateTime> availableSlots = new ArrayList<>();
    @Field(name = "service_area_radius_km", type = FieldType.Double)
    private Double serviceAreaRadiusKm;

    @Field(name = "preferred_languages", type = FieldType.Keyword)
    private List<String> preferredLanguages;

    @Field(name = "minimum_provider_rating", type = FieldType.Double)
    private Double minimumProviderRating;

    @Field(name = "cancellation_policy_preference", type = FieldType.Keyword)
    private String cancellationPolicyPreference;
    @Field(name = "all_text", type = FieldType.Text, analyzer = "standard")
    private String allText;
}



