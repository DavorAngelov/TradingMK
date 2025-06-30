package com.tradingmk.backend.service;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.StockHistory;
import com.tradingmk.backend.repository.StockHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockHistoryService {

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    public void saveAll(List<StockHistory> histories) {
        for (StockHistory incoming : histories) {
            StockHistory history = new StockHistory();
            history.setSymbol(incoming.getSymbol());
            history.setPrice(incoming.getPrice());
            history.setTimestamp(incoming.getTimestamp());
            System.out.println("test saving: " + history.getSymbol() + ", " + history.getPrice() + ", " + history.getTimestamp());
            stockHistoryRepository.save(history);
        }
    }

    public List<StockHistory> getHistoryForSymbol(String symbol, LocalDate from, LocalDate to) {
        return stockHistoryRepository.findBySymbolAndTimestampBetween(symbol, from, to);
    }
}
