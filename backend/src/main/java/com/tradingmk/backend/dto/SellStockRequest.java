package com.tradingmk.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SellStockRequest {
    private String stockSymbol;
    private int quantity;
    private BigDecimal pricePerUnit;
}