package com.example.priceservice.client;

import com.example.priceservice.dto.BinanceApiPriceDto;
import com.example.priceservice.exception.BinanceApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Slf4j
@RequiredArgsConstructor
public class BinanceClient {

    private final RestClient restClient;

    public BinanceApiPriceDto getAvgPrice(String symbol) {
        log.info("Requesting price for symbol: {}", symbol);

        try {
            BinanceApiPriceDto priceResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/avgPrice")
                            .queryParam("symbol", symbol)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, resp) -> {
                        String errorMsg = String.format(
                                "Client error requesting %s: %s",
                                symbol, resp.getStatusCode());
                        log.error(errorMsg);
                        throw new BinanceApiException(errorMsg);
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, resp) -> {
                        String errorMsg = String.format(
                                "Binance server error requesting %s: %s",
                                symbol, resp.getStatusCode());
                        log.error(errorMsg);
                        throw new BinanceApiException(errorMsg);
                    })
                    .body(BinanceApiPriceDto.class);

            log.info("Received price for {}: {}", symbol, priceResponse);
            return priceResponse;

        } catch (BinanceApiException e) {
            throw e;
        } catch (RestClientException e) {
            String errorMsg = "Network error requesting Binance: " + e.getMessage();
            log.error(errorMsg, e);
            throw new BinanceApiException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error requesting Binance: " + e.getMessage();
            log.error(errorMsg, e);
            throw new BinanceApiException(errorMsg, e);
        }
    }
}