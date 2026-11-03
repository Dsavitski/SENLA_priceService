package com.example.priceservice.dto;

import java.math.BigDecimal;

public record AveragePriceResponseDto(
        BigDecimal averagePrice) {
}
