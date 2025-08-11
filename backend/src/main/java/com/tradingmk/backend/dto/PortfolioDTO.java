package com.tradingmk.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioDTO {
    private BigDecimal balance;
    private List<PortfolioHoldingDTO> holdings;

    public PortfolioDTO(BigDecimal balance, List<PortfolioHoldingDTO> holdings) {
        this.balance = balance;
        this.holdings = holdings;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<PortfolioHoldingDTO> getHoldings() {
        return holdings;
    }
}
