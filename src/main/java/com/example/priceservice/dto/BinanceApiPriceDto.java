package com.example.priceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
public record BinanceApiPriceDto(
        @NotBlank(message = "Symbol cannot be empty")
        @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
        String symbol,
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        BigDecimal price,
        @NotNull(message = "Minutes is required")
        @Min(value = 1, message = "Minutes must be >= 1")
        @JsonProperty("mins") Integer minutes,
        @NotNull(message = "CloseTime is required")
        @JsonProperty("closeTime") Long closeTime) {

}