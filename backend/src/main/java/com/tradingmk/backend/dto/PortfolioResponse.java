package com.tradingmk.backend.dto;

import com.tradingmk.backend.model.PortfolioHolding;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        Long portfolioId,
        BigDecimal balance,
        List<PortfolioHolding> holdings
) {}
