package com.tradingmk.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyStockRequest {
    private String stockSymbol;
    private int quantity;
    private BigDecimal pricePerUnit;
}