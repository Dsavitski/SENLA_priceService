package com.example.priceservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public record PriceHistoryRequestDto(
        @NotBlank(message = "Symbol cannot be empty")
        @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
        String symbol,
        @NotNull(message = "From date is required")
        Instant from,
        @NotNull(message = "To date is required")
        Instant to,
        @Min(value = 0, message = "Page must be >= 0")
        Integer page,
        @Min(value = 1, message = "Size must be >= 1")
        Integer size
) {}