package com.mo.core.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuctionBidEvent implements Serializable {

    @JsonProperty("auction_id")
    private Long auctionId;

    @JsonProperty("bid_id")
    private Long bidId;

    @JsonProperty("bidder_id")
    private Long bidderId;

    @JsonProperty("product_id")
    private Long productId;

    private BigDecimal amount;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;
}
