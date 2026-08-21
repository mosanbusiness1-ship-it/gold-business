package com.mo.core.events;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestEvent {
    private Long organisationId;
    private Long productId;
    private Long escrowId;
    private String transactionRef;
    private BigDecimal amount;
    private String currency;
    private String callbackTopic;
    private String action;
    private String description;
}
