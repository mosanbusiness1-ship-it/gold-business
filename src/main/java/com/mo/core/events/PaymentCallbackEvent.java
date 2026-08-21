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
public class PaymentCallbackEvent {
    private String transactionRef;
    private String status; // SUCCESS, FAILED, PENDING
    private String action; // HOLD, RELEASE, REFUND
    private BigDecimal amount;
    private String currency;
    private String reason;
    private String timestamp; // ISO 8601
}
