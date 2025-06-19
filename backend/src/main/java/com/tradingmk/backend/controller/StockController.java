package com.tradingmk.backend.controller;


import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.service.StockService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks(){
        return stockService.getAllStocks();
    }

    @PostMapping("/update")
    public void updateStocks(@RequestBody List<Stock> stocks){
        stockService.updateStockPrices(stocks);
    }
}
