package com.tradingmk.backend.dto;

import java.math.BigDecimal;

public class PortfolioHoldingDTO {
    private String stockSymbol;
    private int quantity;
    private BigDecimal avgPrice;

    public PortfolioHoldingDTO(String stockSymbol, int quantity, BigDecimal avgPrice) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }
}
