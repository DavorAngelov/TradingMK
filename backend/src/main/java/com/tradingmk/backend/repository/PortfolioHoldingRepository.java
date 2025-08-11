package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.PortfolioHolding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding,Long> {
    List<PortfolioHolding> findByPortfolioId(Long portfolioId);
    Optional<PortfolioHolding> findByPortfolioIdAndStockSymbol(Long portfolioId, String stockSymbol);
}
