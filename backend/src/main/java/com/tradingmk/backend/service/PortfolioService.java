package com.tradingmk.backend.service;

import com.tradingmk.backend.model.Portfolio;
import com.tradingmk.backend.model.PortfolioHolding;
import com.tradingmk.backend.repository.PortfolioHoldingRepository;
import com.tradingmk.backend.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioHoldingRepository holdingRepository;

    public Portfolio getPortfolioByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("pportfolio not found for user-id " + userId));
    }

    public List<PortfolioHolding> getHoldings(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId);
    }


    //need logic for buying a stock
    @Transactional
    public void buyStock(Long portfolioId, String stockSymbol, int quantity, BigDecimal pricePerUnit) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));


        //multiply for how much stocks are bught
        BigDecimal totalCost = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

        if (portfolio.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("not enough balance to buy stock");
        }

        portfolio.setBalance(portfolio.getBalance().subtract(totalCost));

        PortfolioHolding holding = holdingRepository
                .findByPortfolioIdAndStockSymbol(portfolioId, stockSymbol)
                .orElse(PortfolioHolding.builder()
                        .portfolio(portfolio)
                        .stockSymbol(stockSymbol)
                        .quantity(0)
                        .avgPrice(BigDecimal.ZERO)
                        .build());

        // avg price bought
        // alkaloid 2 x 22.000 + alkaloid 3x 28.000 average od ova
        BigDecimal currentTotalValue = holding.getAvgPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
        BigDecimal newTotalValue = currentTotalValue.add(totalCost);
        int newQuantity = holding.getQuantity() + quantity;
        BigDecimal newAvgPrice = newTotalValue.divide(BigDecimal.valueOf(newQuantity), BigDecimal.ROUND_HALF_UP);

        holding.setQuantity(newQuantity);
        holding.setAvgPrice(newAvgPrice);

        holdingRepository.save(holding);
        portfolioRepository.save(portfolio);
    }

    //sell
    @Transactional
    public void sellStock(Long portfolioId, String stockSymbol, int quantity, BigDecimal pricePerUnit) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("portfolio not found"));

        PortfolioHolding holding = holdingRepository
                .findByPortfolioIdAndStockSymbol(portfolioId, stockSymbol)
                .orElseThrow(() -> new RuntimeException("stock not found in portfolio"));

        if (holding.getQuantity() < quantity) {
            throw new RuntimeException("not enough shares to sell");
        }

        BigDecimal totalGain = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
        holding.setQuantity(holding.getQuantity() - quantity);

        if (holding.getQuantity() == 0) {
            holdingRepository.delete(holding);
        } else {
            holdingRepository.save(holding);
        }

        portfolio.setBalance(portfolio.getBalance().add(totalGain));
        portfolioRepository.save(portfolio);
    }
}
