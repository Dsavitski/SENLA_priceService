package com.example.priceservice.scheduler;

import com.example.priceservice.client.BinanceClient;
import com.example.priceservice.dto.BinanceApiPriceDto;
import com.example.priceservice.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinancePriceScheduler {

    private final BinanceClient binanceClient;
    private final PriceService priceService;

    @Value("${binance.default.symbol}")
    private String defaultSymbol;

    @Scheduled(fixedDelayString = "${binance.poll.interval}")
    public void fetchAndSavePrice() {
        log.info("Starting scheduled price fetch for symbol: {}", defaultSymbol);

        try {
            BinanceApiPriceDto priceData = binanceClient.getAvgPrice(defaultSymbol);


            priceService.savePrice(defaultSymbol, priceData);

            log.info("Successfully fetched and saved price for {}", defaultSymbol);

        } catch (Exception e) {
            log.error("Failed to fetch or save price for {}. Scheduler will retry at next interval. Error: {}",
                    defaultSymbol, e.getMessage(), e);
        }
    }
}