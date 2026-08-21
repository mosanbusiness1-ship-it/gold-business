package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mo.core.enums.NeedType;
import com.mo.core.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, // On utilise la propriété "type"
    property = "type",
    visible = true // Nécessaire pour que Jackson passe la valeur à l'objet
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ServiceNeedDto.class, name = "SERVICE"),
    @JsonSubTypes.Type(value = VehicleNeedDto.class, name = "VEHICLE"),
    @JsonSubTypes.Type(value = ElectronicNeedDto.class, name = "ELECTRONIC"),
    @JsonSubTypes.Type(value = FashionNeedDto.class, name = "FASHION"),
    @JsonSubTypes.Type(value = FoodNeedDto.class, name = "FOOD"),
    @JsonSubTypes.Type(value = RealEstateNeedDto.class, name = "REALESTATE")
})
@Data @AllArgsConstructor @NoArgsConstructor
/**
 * AbstractUserNeedDto
 *
 * Purpose: base DTO for user-created needs (requests) on the platform. Frontend
 * developers should use concrete subclasses when rendering, creating or
 * updating needs (ServiceNeedDto, VehicleNeedDto, ElectronicNeedDto, etc.).
 *
 * Key fields:
 * - `name` (String): short title for the need.
 * - `maxPrice` (BigDecimal): maximum acceptable price for suppliers.
 * - `quantity` (int): requested quantity.
 * - `userId` (Long): id of the user who created the need.
 * - `description` (String): detailed description to help sellers respond.
 * - `type` (NeedType): discriminator used for polymorphic deserialization.
 * - `photoUrls` (List<String>): optional images to illustrate the need.
 * - `currency` (Currency): ISO-4217 currency code for `maxPrice`.
 *
 * Frontend guidance:
 * - When sending needs to the backend, include the `type` field so the server
 *   can instantiate the correct concrete DTO.
 * - Use `maxPrice` + `currency` to display budget information.
 */
public abstract class AbstractUserNeedDto {


    private String name;

    @JsonProperty("max_price")
    private BigDecimal maxPrice;
    
    private int quantity;


    @JsonProperty("user_id")
    private Long userId;

    private String description;

    private NeedType type;

    @JsonProperty("photo_urls")
    private List<String> photoUrls = new ArrayList<>();

    @Schema(description = "ISO 4217 currency code", example = "XAF")
    private Currency currency;

}

