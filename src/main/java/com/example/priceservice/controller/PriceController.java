package com.example.priceservice.controller;

import com.example.priceservice.dto.*;
import com.example.priceservice.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;
    @GetMapping("/latest")
    public ResponseEntity<PriceToUserResponseDto> getLatestPrice(
            @RequestParam(required = false, defaultValue = "BNBUSDT") String symbol
    ) {
        return ResponseEntity.ok(priceService.getLatestPrice(symbol));
    }

    @GetMapping("/average")
    public ResponseEntity<AveragePriceResponseDto> getAveragePrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false, defaultValue = "BNBUSDT") String symbol
    ) {
        return ResponseEntity.ok(priceService.getAveragePrice(symbol, from, to));
    }

    @GetMapping
    public ResponseEntity<PaginatedPriceResponseDto> getPriceHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "BNBUSDT") String symbol
    ) {
        return ResponseEntity.ok(priceService.getPriceHistory(symbol, from, to, page, size));
    }

    @GetMapping("/trend")
    public ResponseEntity<TrendResponseDto> getTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false, defaultValue = "BNBUSDT") String symbol
    ) {
        return ResponseEntity.ok(priceService.getTrend(symbol, from, to));
    }
}