package com.tradingmk.backend.service;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.StockHistory;
import com.tradingmk.backend.repository.StockHistoryRepository;
import com.tradingmk.backend.repository.StockRepository;
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

    @Autowired
    private StockRepository stockRepository;

    public void saveAll(List<StockHistory> histories) {

        for (StockHistory incoming : histories) {

            Stock stock = stockRepository
                    .findBySymbol(incoming.getStock().getSymbol())
                    .orElseThrow(() ->
                            new RuntimeException("Stock not found: " + incoming.getStock().getSymbol()));

            StockHistory history = new StockHistory();
            history.setStock(stock);
            history.setPrice(incoming.getPrice());
            history.setTimestamp(incoming.getTimestamp());

            stockHistoryRepository.save(history);
        }
    }

    public List<StockHistory> getHistoryForSymbol(String symbol, LocalDate from, LocalDate to) {

        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));

        return stockHistoryRepository
                .findByStockAndTimestampBetween(stock, from, to);
    }

    /**
     * Analyzes a list of historical prices (oldest -> newest) and returns
     * the overall trend based on the last observed price movement.
     * Used for Graph Coverage
     */
    public String analyzeTrend(List<Double> prices) {
        double temps = 0;
        String trend = "STABLE";
        for (int i = 1; i < prices.size(); i++) {
            temps = prices.get(i) - prices.get(i - 1);
            if (temps > 0) {
                trend = "UP";
            } else if (temps < 0) {
                trend = "DOWN";
            }
        }
        return trend;
    }

}
