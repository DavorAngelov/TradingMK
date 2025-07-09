package com.tradingmk.backend.controller;
import com.tradingmk.backend.model.StockHistory;
import com.tradingmk.backend.repository.StockHistoryRepository;
import com.tradingmk.backend.service.StockHistoryService;
import com.tradingmk.backend.service.StockService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/history")
public class StockHistoryController {

    @Autowired
    private StockHistoryService stockHistoryService;

    @PostMapping("/upload")
    public ResponseEntity<Void> saveHistory(@RequestBody List<StockHistory> histories) {
        stockHistoryService.saveAll(histories);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{symbol}")
    public List<StockHistory> getHistory(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return stockHistoryService.getHistoryForSymbol(symbol, from, to);
    }
}
