package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock,Long> {
    Optional<Stock> findBySymbol(String symbol);

}
