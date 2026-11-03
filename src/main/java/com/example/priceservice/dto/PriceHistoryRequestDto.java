package com.example.priceservice.dto;

import java.time.Instant;

public record PriceHistoryRequestDto(
        String symbol,
        Instant from,
        Instant to,
        Integer page,
        Integer size) {

}
