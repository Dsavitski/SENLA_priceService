package com.example.priceservice.controller;

import com.example.priceservice.dto.AveragePriceResponseDto;
import com.example.priceservice.dto.BinanceApiPriceDto;
import com.example.priceservice.dto.PaginatedPriceResponseDto;
import com.example.priceservice.dto.PriceToUserResponseDto;
import com.example.priceservice.dto.TrendResponseDto;
import com.example.priceservice.service.PriceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
@Validated
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/latest")
    public ResponseEntity<PriceToUserResponseDto> getLatestPrice(
            @RequestParam(required = false, defaultValue = "BNBUSDT")
            @NotBlank(message = "Symbol cannot be empty")
            @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
            String symbol) {
        return ResponseEntity.ok(priceService.getLatestPrice(symbol));
    }

    @GetMapping("/average")
    public ResponseEntity<AveragePriceResponseDto> getAveragePrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false, defaultValue = "BNBUSDT")
            @NotBlank(message = "Symbol cannot be empty")
            @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
            String symbol) {
        return ResponseEntity.ok(priceService.getAveragePrice(symbol, from, to));
    }

    @GetMapping
    public ResponseEntity<PaginatedPriceResponseDto> getPriceHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page must be >= 0")
            int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Size must be >= 1")
            int size,
            @RequestParam(required = false, defaultValue = "BNBUSDT")
            @NotBlank(message = "Symbol cannot be empty")
            @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
            String symbol) {
        return ResponseEntity.ok(priceService.getPriceHistory(symbol, from, to, page, size));
    }

    @GetMapping("/trend")
    public ResponseEntity<TrendResponseDto> getTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false, defaultValue = "BNBUSDT")
            @NotBlank(message = "Symbol cannot be empty")
            @Pattern(regexp = "^[A-Z]{4,10}$", message = "Symbol must consist of 4-10 uppercase Latin letters")
            String symbol) {
        return ResponseEntity.ok(priceService.getTrend(symbol, from, to));
    }
    @PostMapping
    public ResponseEntity<Void> savePrice(@Valid @RequestBody BinanceApiPriceDto dto) {
        priceService.savePrice(dto.symbol(), dto);
        return ResponseEntity.ok().build();
    }
}