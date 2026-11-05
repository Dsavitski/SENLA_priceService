package com.example.priceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record BinanceApiPriceDto(
        String symbol,
        BigDecimal price,
        @JsonProperty("mins") Integer minutes,
        @JsonProperty("closeTime") Long closeTime) {

}
