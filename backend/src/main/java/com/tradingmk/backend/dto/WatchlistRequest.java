package com.tradingmk.backend.dto;

import lombok.Data;

@Data
public class WatchlistRequest {
    private String symbol;
    private Double priceAbove;
    private Double priceBelow;



    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPriceAbove() {
        return priceAbove;
    }

    public void setPriceAbove(Double priceAbove) {
        this.priceAbove = priceAbove;
    }

    public Double getPriceBelow() {
        return priceBelow;
    }

    public void setPriceBelow(Double priceBelow) {
        this.priceBelow = priceBelow;
    }
}
