package com.tradingmk.backend.dto;

import java.time.LocalDateTime;

public class StockDTO {
    private String symbol;
    private String name;
    private Double currentPrice;
    private Double lastPrice;
    private LocalDateTime lastUpdated;

    public StockDTO(String symbol, String name, Double currentPrice,
                    Double lastPrice, LocalDateTime lastUpdated) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.lastPrice = lastPrice;
        this.lastUpdated = lastUpdated;
    }

    // getters

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}