package com.example.priceservice.repository;

import com.example.priceservice.entity.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {
    
}
