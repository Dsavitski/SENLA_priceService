package com.example.priceservice.service;

import com.example.priceservice.dto.*;
import com.example.priceservice.entity.PriceSnapshot;
import com.example.priceservice.entity.Trend;
import com.example.priceservice.exception.ResourceNotFoundException;
import com.example.priceservice.exception.ValidException;
import com.example.priceservice.mapper.PriceSnapshotMapper;
import com.example.priceservice.repository.PriceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private final PriceSnapshotRepository repository;
    private final PriceSnapshotMapper mapper;

    @Transactional
    public void savePrice(String symbol, BinanceApiPriceDto dto) {
        Instant binanceCloseTime = Instant.ofEpochMilli(dto.closeTime());
        Instant fetchedAt = Instant.now();

        if (repository.existsBySymbolAndBinanceCloseTime(symbol, binanceCloseTime)) {
            log.info("Price for {} at {} already exists, skipping save.", symbol, binanceCloseTime);
            return;
        }

        PriceSnapshot snapshot = new PriceSnapshot();
        snapshot.setSymbol(symbol);
        snapshot.setPrice(dto.price());
        snapshot.setBinanceCloseTime(binanceCloseTime);
        snapshot.setFetchedAt(fetchedAt);

        repository.save(snapshot);
        log.debug("Saved new price snapshot for {}", symbol);
    }

    public PriceToUserResponseDto getLatestPrice(String symbol) {
        return repository.findTopBySymbolOrderByFetchedAtDesc(symbol)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No price data found for symbol: " + symbol
                ));
    }

    public AveragePriceResponseDto getAveragePrice(String symbol, Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new ValidException("'from' date must be before or equal to 'to' date");
        }

        BigDecimal average = repository.findAveragePriceBySymbolAndFetchedAtBetween(symbol, from, to);

        if (average == null) {
            throw new ResourceNotFoundException("No price data found for the specified period");
        }

        return new AveragePriceResponseDto(average);
    }

    public PaginatedPriceResponseDto getPriceHistory(String symbol, Instant from, Instant to, int page, int size) {
        if (from.isAfter(to)) {
            throw new ValidException("'from' date must be before or equal to 'to' date");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<PriceSnapshot> pageEntity = repository.findBySymbolAndFetchedAtBetween(symbol, from, to, pageable);

        return mapper.toPaginatedResponseDto(pageEntity);
    }

    public TrendResponseDto getTrend(String symbol, Instant from, Instant to) {
        if (from.isAfter(to)) {
            throw new ValidException("'from' date must be before or equal to 'to' date");
        }

        List<PriceSnapshot> history = repository.findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc(symbol, from, to);

        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No price data found for the specified period to calculate trend");
        }

        BigDecimal startPrice = history.getFirst().getPrice();
        BigDecimal endPrice = history.getLast().getPrice();

        Trend trend;
        int comparison = endPrice.compareTo(startPrice);
        if (comparison > 0) {
            trend = Trend.UPWARD;
        } else if (comparison < 0) {
            trend = Trend.DOWNWARD;
        } else {
            trend = Trend.FLAT;
        }

        return new TrendResponseDto(symbol, from, to, trend);
    }
}