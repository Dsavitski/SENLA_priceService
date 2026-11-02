package com.example.priceservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "price_snaphots")
@Getter
@Setter
@NoArgsConstructor
public class PriceSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal price;

    @Column(name = "binance_close_time", nullable = false)
    private Instant binanceCloseTime;

    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;
}
