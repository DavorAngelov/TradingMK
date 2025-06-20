package com.tradingmk.backend.service;


import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public void updateStockPrices(List<Stock> stocks) {
        for (Stock incoming : stocks) {
            Optional<Stock> existing = stockRepository.findBySymbol(incoming.getSymbol());

            if (existing.isPresent()) {
                Stock stock = existing.get();
                stock.setLastPrice(incoming.getLastPrice());
                stock.setName(incoming.getName());
                stock.setLastUpdated(LocalDateTime.now());
                if (incoming.getPercentage() == null) {
                    incoming.setPercentage(0.0);
                }
                stock.setPercentage(incoming.getPercentage());
                stock.setTurnover(incoming.getTurnover());
                stock.setCurrentPrice(incoming.getCurrentPrice());
                System.out.println("Received stock: " + stock.getSymbol() + " with currentPrice: " + stock.getCurrentPrice());
                System.out.println("Before save: " + stock.getCurrentPrice());
                stockRepository.save(stock);
                System.out.println("afterr save: " + stock.getCurrentPrice());
            } else {
                incoming.setLastUpdated(LocalDateTime.now());

                stockRepository.save(incoming);
            }
        }
    }
}
