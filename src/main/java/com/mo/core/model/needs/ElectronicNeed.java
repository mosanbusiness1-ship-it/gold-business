package com.mo.core.model.needs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mo.core.enums.ElectronicType;
import com.mo.core.visitors.need_visitors.UserNeedVisitor;

import jakarta.persistence.*;
import lombok.*;

@Entity
//@DiscriminatorValue("ELECTRONIC")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public
class ElectronicNeed extends AbstractUserNeed {

    @Enumerated(EnumType.STRING)
    @JsonProperty("electronic_type")
    private ElectronicType electronicType;

    @JsonProperty("electronic_brand")
    private String electronicBrand;

    @JsonProperty("electronic_model")
    private String electronicModel;

    private String specifications;

    @JsonProperty("warranty_period")
    private String warrantyPeriod;
    
    @JsonProperty("min_storage_gb")
    private Integer minStorageGB;
    
    @JsonProperty("min_ram_gb")
    private Integer minRAMGB;
    
    @JsonProperty("preferred_os")
    private String preferredOS;
    
    @JsonProperty("max_age_years")
    private Integer maxAgeYears;
    
    @JsonProperty("warranty_required")
    private Boolean warrantyRequired = false;

    @Override
    public <R> R accept(UserNeedVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
