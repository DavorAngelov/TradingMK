package com.tradingmk.backend.controller;


import com.tradingmk.backend.dto.StockDTO;
import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.service.StockService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {

    @Autowired
    private StockService stockService;

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @GetMapping
    public List<StockDTO> getAllStocks() {
        return stockRepository.findAll()
                .stream()
                .map(stock -> new StockDTO(
                        stock.getSymbol(),
                        stock.getName(),
                        stock.getCurrentPrice(),
                        stock.getLastPrice(),
                        stock.getLastUpdated()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/update")
    public void updateStocks(@RequestBody List<Stock> stocks){
        stockService.updateStockPrices(stocks);
    }
}
