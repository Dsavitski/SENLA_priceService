package com.example.priceservice.dto;

import java.util.List;

public record PaginatedPriceResponseDto(
        List<PriceToUserResponseDto> items,
        int page,
        int size,
        long totalElements,
        boolean hasNext
) {}