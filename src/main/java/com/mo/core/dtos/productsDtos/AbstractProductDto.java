package com.mo.core.dtos.productsDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.mo.core.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mo.configuration.CustomLocalDateTimeDeserializer;
import com.mo.core.enums.ProductType;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, // On utilise la propriété "type"
    property = "type",
    visible = true // Nécessaire pour que Jackson passe la valeur à l'objet
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ServiceProductDto.class, name = "SERVICE"),
    @JsonSubTypes.Type(value = VehicleProductDto.class, name = "VEHICLE"),
    @JsonSubTypes.Type(value = ElectronicProductDto.class, name = "ELECTRONIC"),
    @JsonSubTypes.Type(value = FashionProductDto.class, name = "FASHION"),
    @JsonSubTypes.Type(value = FoodProductDto.class, name = "FOOD"),
    @JsonSubTypes.Type(value = RealEstateProductDto.class, name = "REALESTATE")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * AbstractProductDto
 *
 * Purpose: base DTO for all product types exposed by the backend API. Frontend
 * code should use concrete subclasses (ServiceProductDto, VehicleProductDto,
 * ElectronicProductDto, FashionProductDto, FoodProductDto, RealEstateProductDto)
 * when rendering or editing specific product categories.
 *
 * Key fields and their roles:
 * - `ownerId` (Long): id of the product owner/seller.
 * - `name` / `description` (String): display title and full description.
 * - `price` (BigDecimal) / `currency` (Currency): price and ISO-4217 currency.
 * - `quantity` (int): available quantity where applicable.
 * - `photoUrls` (List<String>): images to display in gallery components.
 * - `createdAt` / `updatedAt` (LocalDateTime): timestamps for audit and sorting.
 * - `enabled` / `isPlatformOwner` / `version`: flags and versioning information.
 *
 * Frontend guidance:
 * - Deserialize polymorphic types using the `type` property (the controller
 *   returns concrete subtype JSON with `type` set to values such as SERVICE,
 *   VEHICLE, ELECTRONIC...). The Jackson annotations in this class mirror
 *   server-side polymorphic handling.
 * - Display `price` using the `currency` field. Use locale-aware formatting
 *   when rendering monetary values.
 * - When creating or updating products, send payloads matching the concrete
 *   product DTO expected by the backend for that product `type`.
 */
public abstract class AbstractProductDto {

    @JsonProperty("owner_id")
    private Long ownerId;

    private String name;

    private String description;

    private BigDecimal price;
    
    private int quantity;
    
    @Schema(description = "ISO 4217 currency code", example = "XAF")
    private Currency currency;
    
    protected ProductType type;

    @JsonProperty("photo_urls")
    private List<String> photoUrls;

    @JsonProperty("created_at")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    private Boolean enabled;

    @JsonProperty("is_platform_owner")
    private Boolean isPlatformOwner;

    private Integer version;

    private Boolean certified;

}
