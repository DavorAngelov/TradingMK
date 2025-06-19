package com.tradingmk.backend.service;


import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public void updateStockPrices(List<Stock> stocks) {
        for (Stock newStock : stocks) {
            stockRepository.findBySymbol(newStock.getSymbol()).ifPresentOrElse(existing -> {
                existing.setCurrentPrice(newStock.getCurrentPrice());
                existing.setLastUpdated(LocalDateTime.now());
                stockRepository.save(existing);
            }, () -> {
                newStock.setLastUpdated(LocalDateTime.now());
                stockRepository.save(newStock);
            });
        }
    }
}
