package com.mo.core.dtos.userNeedsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ElectronicType;
import com.mo.core.enums.NeedType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * ElectronicNeedDto
 *
 * Purpose: DTO for electronic-related needs (phones, laptops, etc.). Extends
 * `AbstractUserNeedDto` with electronics-specific fields that help suppliers
 * match products to buyer requirements.
 *
 * Notable fields:
 * - `mandatoryFields` (List<String>): list of key fields the seller should provide.
 * - `electronicType` (ElectronicType): subtype classification.
 * - `brand`, `model`, `specifications`, `warrantyPeriod`: details to improve matching.
 *
 * Frontend guidance:
 * - Use `mandatoryFields` to highlight required input when sellers respond.
 */
public class ElectronicNeedDto extends AbstractUserNeedDto {

    @JsonProperty("mandatory_fields")
    private List<String> mandatoryFields = new ArrayList<>();


    @JsonProperty("electronic_type")
    private ElectronicType electronicType;
    
    private String brand;
    private String model;
    private String specifications; // JSON string ou autre format simple

    @JsonProperty("warranty_period")
    private String warrantyPeriod;

}