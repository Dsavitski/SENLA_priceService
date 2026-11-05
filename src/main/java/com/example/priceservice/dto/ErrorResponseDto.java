package com.example.priceservice.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponseDto(
    Instant timestamp,
    int status,
    String error,
    String message) {
    public static ErrorResponseDto createErrorResponse(HttpStatus status, String message){
        return new ErrorResponseDto(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }
}
