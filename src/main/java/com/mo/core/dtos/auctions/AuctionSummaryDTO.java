package com.mo.core.dtos.auctions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuctionSummaryDTO {

    @JsonProperty("auction_id")
    private Long auctionId;

    @JsonProperty("current_highest_bid")
    private BigDecimal currentHighestBid;

    @JsonProperty("current_highest_bidder_id")
    private Long currentHighestBidderId;

    @JsonProperty("current_winner_id")
    private Long currentWinnerId;

    @JsonProperty("bid_count")
    private Long bidCount;

    private String status;

    @JsonProperty("ends_at")
    private LocalDateTime endsAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
