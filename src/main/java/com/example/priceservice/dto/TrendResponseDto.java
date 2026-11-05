package com.example.priceservice.dto;

import com.example.priceservice.entity.Trend;

import java.time.Instant;

public record TrendResponseDto(
        String symbol,
        Instant startDate,
        Instant endDate,
        Trend trend) {
}
