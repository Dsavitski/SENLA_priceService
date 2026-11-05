package com.example.priceservice.repository;

import com.example.priceservice.entity.PriceSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {

    Optional<PriceSnapshot> findTopBySymbolOrderByFetchedAtDesc(String symbol);


    Page<PriceSnapshot> findBySymbolAndFetchedAtBetween(
            String symbol,
            Instant from,
            Instant to,
            Pageable pageable
    );

    @Query(value = """
        SELECT AVG(price) 
        FROM price_snapshots 
        WHERE symbol = :symbol 
          AND fetched_at BETWEEN :from AND :to
        """, nativeQuery = true)
    BigDecimal findAveragePriceBySymbolAndFetchedAtBetween(
            @Param("symbol") String symbol,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    List<PriceSnapshot> findBySymbolAndFetchedAtBetweenOrderByFetchedAtAsc(
            String symbol,
            Instant from,
            Instant to
    );



    boolean existsBySymbolAndBinanceCloseTime(String symbol, Instant binanceCloseTime);
}