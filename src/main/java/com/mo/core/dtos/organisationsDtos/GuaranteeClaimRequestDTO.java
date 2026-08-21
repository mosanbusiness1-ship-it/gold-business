package com.mo.core.dtos.organisationsDtos;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@Schema(name = "GuaranteeClaimRequest", description = "Payload to create a guarantee claim for a product")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
/**
 * GuaranteeClaimRequestDTO
 *
 * Purpose: DTO for filing a guarantee claim when a purchased product has issues
 * (defect, damage, etc.). Initiates the warranty/guarantee process.
 *
 * Important fields:
 * - `productId` (Long): the product covered under guarantee.
 * - `reason` (String): explanation of the issue (e.g., damaged, defective).
 *
 * Frontend guidance:
 * - Present a form to describe the issue and optionally attach images.
 * - Show the guarantee policy terms before allowing claim submission.
 */
public class GuaranteeClaimRequestDTO {

    @NotNull
    @Schema(description = "Product identifier under guarantee", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "Reason for the guarantee claim", example = "Item arrived damaged")
    private String reason;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}