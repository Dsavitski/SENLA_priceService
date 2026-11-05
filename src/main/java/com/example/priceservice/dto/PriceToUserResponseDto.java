package com.example.priceservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PriceToUserResponseDto(
        String symbol,
        BigDecimal price,
        Instant fetchedAt) {
}
