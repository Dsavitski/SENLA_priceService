package com.example.priceservice.controller;

import com.example.priceservice.entity.PriceSnapshot;
import com.example.priceservice.repository.PriceSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PriceControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PriceSnapshotRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        PriceSnapshot snapshot = new PriceSnapshot();
        snapshot.setSymbol("BNBUSDT");
        snapshot.setPrice(new BigDecimal("699.50"));
        snapshot.setBinanceCloseTime(Instant.parse("2026-11-05T12:00:00Z"));
        snapshot.setFetchedAt(Instant.parse("2026-11-05T12:00:05Z"));

        repository.save(snapshot);
    }

    @Test
    void getLatestPrice_shouldReturn200AndCorrectData() throws Exception {
        mockMvc.perform(get("/api/v1/prices/latest")
                        .param("symbol", "BNBUSDT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("BNBUSDT"))
                .andExpect(jsonPath("$.price").value(699.50))
                .andExpect(jsonPath("$.fetchedAt").isNotEmpty());
    }

    @Test
    void getLatestPrice_shouldReturn404WhenSymbolNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/prices/latest")
                        .param("symbol", "NONEXISTENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}