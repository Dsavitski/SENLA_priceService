package com.example.priceservice.service;

import com.example.priceservice.dto.*;
import com.example.priceservice.entity.PriceSnapshot;
import com.example.priceservice.entity.Trend;
import com.example.priceservice.exception.ResourceNotFoundException;
import com.example.priceservice.exception.ValidException;
import com.example.priceservice.mapper.PriceSnapshotMapper;
import com.example.priceservice.repository.PriceSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceSnapshotRepository repository;

    @Mock
    private PriceSnapshotMapper mapper;

    @InjectMocks
    private PriceService priceService;

    private PriceSnapshot snapshot;
    private BinanceApiPriceDto binanceDto;

    @BeforeEach
    void setUp() {
        snapshot = new PriceSnapshot();
        snapshot.setId(1L);
        snapshot.setSymbol("BNBUSDT");
        snapshot.setPrice(new BigDecimal("600.50"));
        snapshot.setBinanceCloseTime(Instant.parse("2026-11-05T12:00:00Z"));
        snapshot.setFetchedAt(Instant.parse("2026-11-05T12:00:05Z"));

        binanceDto = new BinanceApiPriceDto(
                "BNBUSDT",
                new BigDecimal("600.50"),
                5,
                1730808000000L
        );
    }



    @Test
    void savePrice_shouldSaveNewRecord() {
        when(repository.existsBySymbolAndBinanceCloseTime(eq("BNBUSDT"), any(Instant.class))).thenReturn(false);
        when(repository.save(any(PriceSnapshot.class))).thenReturn(snapshot);

        priceService.savePrice("BNBUSDT", binanceDto);

        verify(repository, times(1)).existsBySymbolAndBinanceCloseTime(eq("BNBUSDT"), any(Instant.class));
        verify(repository, times(1)).save(any(PriceSnapshot.class));
    }

    @Test
    void savePrice_shouldSkipDuplicate() {
        when(repository.existsBySymbolAndBinanceCloseTime(eq("BNBUSDT"), any(Instant.class))).thenReturn(true);

        priceService.savePrice("BNBUSDT", binanceDto);

        verify(repository, times(1)).existsBySymbolAndBinanceCloseTime(eq("BNBUSDT"), any(Instant.class));
        verify(repository, never()).save(any());
    }

    @Test
    void getLatestPrice_shouldReturnDto() {
        PriceToUserResponseDto expectedDto = new PriceToUserResponseDto(
                "BNBUSDT", new BigDecimal("600.50"), Instant.parse("2026-11-05T12:00:05Z")
        );
        when(repository.findTopBySymbolOrderByFetchedAtDesc("BNBUSDT")).thenReturn(Optional.of(snapshot));
        when(mapper.toResponseDto(snapshot)).thenReturn(expectedDto);

        PriceToUserResponseDto result = priceService.getLatestPrice("BNBUSDT");

        assertNotNull(result);
        assertEquals("BNBUSDT", result.symbol());
        verify(mapper, times(1)).toResponseDto(snapshot);
    }

    @Test
    void getLatestPrice_shouldThrowWhenNotFound() {
        when(repository.findTopBySymbolOrderByFetchedAtDesc("UNKNOWN")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> priceService.getLatestPrice("UNKNOWN")
        );
        assertTrue(ex.getMessage().contains("UNKNOWN"));
    }

    @Test
    void getAveragePrice_shouldReturnDto() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");
        BigDecimal avg = new BigDecimal("600.75");

        when(repository.findAveragePriceBySymbolAndFetchedAtBetween("BNBUSDT", from, to)).thenReturn(avg);

        AveragePriceResponseDto result = priceService.getAveragePrice("BNBUSDT", from, to);

        assertEquals(avg, result.averagePrice());
    }

    @Test
    void getAveragePrice_shouldThrowWhenNoData() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        when(repository.findAveragePriceBySymbolAndFetchedAtBetween("BNBUSDT", from, to)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> priceService.getAveragePrice("BNBUSDT", from, to));
    }

    @Test
    void getAveragePrice_shouldThrowWhenFromAfterTo() {
        Instant from = Instant.parse("2026-11-05T13:00:00Z");
        Instant to = Instant.parse("2026-11-05T12:00:00Z");

        assertThrows(ValidException.class, () -> priceService.getAveragePrice("BNBUSDT", from, to));
    }

    @Test
    void getPriceHistory_shouldReturnPaginatedDto() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        Page<PriceSnapshot> pageMock = new PageImpl<>(List.of(snapshot), PageRequest.of(0, 10), 1);
        PaginatedPriceResponseDto expectedDto = new PaginatedPriceResponseDto(List.of(), 0, 10, 1L, false);

        when(repository.findBySymbolAndFetchedAtBetween(eq("BNBUSDT"), eq(from), eq(to), any())).thenReturn(pageMock);
        when(mapper.toPaginatedResponseDto(pageMock)).thenReturn(expectedDto);

        PaginatedPriceResponseDto result = priceService.getPriceHistory("BNBUSDT", from, to, 0, 10);

        assertNotNull(result);
        verify(mapper, times(1)).toPaginatedResponseDto(pageMock);
    }

    @Test
    void getPriceHistory_shouldThrowWhenFromAfterTo() {
        Instant from = Instant.parse("2026-11-05T13:00:00Z");
        Instant to = Instant.parse("2026-11-05T12:00:00Z");

        assertThrows(ValidException.class, () -> priceService.getPriceHistory("BNBUSDT", from, to, 0, 10));
    }

    @Test
    void getTrend_shouldReturnUpward() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        PriceSnapshot first = new PriceSnapshot();
        first.setPrice(new BigDecimal("600.00"));

        PriceSnapshot last = new PriceSnapshot();
        last.setPrice(new BigDecimal("650.00"));

        when(repository.findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc("BNBUSDT", from, to))
                .thenReturn(List.of(first, last));

        TrendResponseDto result = priceService.getTrend("BNBUSDT", from, to);

        assertEquals(Trend.UPWARD, result.trend());
    }

    @Test
    void getTrend_shouldReturnDownward() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        PriceSnapshot first = new PriceSnapshot();
        first.setPrice(new BigDecimal("650.00"));

        PriceSnapshot last = new PriceSnapshot();
        last.setPrice(new BigDecimal("600.00"));

        when(repository.findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc("BNBUSDT", from, to))
                .thenReturn(List.of(first, last));

        TrendResponseDto result = priceService.getTrend("BNBUSDT", from, to);

        assertEquals(Trend.DOWNWARD, result.trend());
    }

    @Test
    void getTrend_shouldReturnFlat() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        PriceSnapshot first = new PriceSnapshot();
        first.setPrice(new BigDecimal("600.00"));

        PriceSnapshot last = new PriceSnapshot();
        last.setPrice(new BigDecimal("600.00"));

        when(repository.findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc("BNBUSDT", from, to))
                .thenReturn(List.of(first, last));

        TrendResponseDto result = priceService.getTrend("BNBUSDT", from, to);

        assertEquals(Trend.FLAT, result.trend());
    }

    @Test
    void getTrend_shouldThrowWhenEmpty() {
        Instant from = Instant.parse("2026-11-05T12:00:00Z");
        Instant to = Instant.parse("2026-11-05T13:00:00Z");

        when(repository.findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc("BNBUSDT", from, to))
                .thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> priceService.getTrend("BNBUSDT", from, to));
    }

    @Test
    void getTrend_shouldThrowWhenFromAfterTo() {
        Instant from = Instant.parse("2026-11-05T13:00:00Z");
        Instant to = Instant.parse("2026-11-05T12:00:00Z");

        assertThrows(ValidException.class, () -> priceService.getTrend("BNBUSDT", from, to));
    }
}