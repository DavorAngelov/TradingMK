package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findBySymbolAndTimestampBetween(String symbol, LocalDate from, LocalDate to);

}
