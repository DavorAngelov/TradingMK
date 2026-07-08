package com.tradingmk.backend.service;

import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.PortfolioHoldingRepository;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioHoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;

    public Portfolio getPortfolioByUserId(Long userId) {
        return portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user-id " + userId));
    }

    public List<PortfolioHolding> getHoldings(Long portfolioId) {
        return holdingRepository.findByPortfolioId(portfolioId);
    }

    /**
     * Entry point called by TradeRequestController on approval.
     * Routes to buyStock or sellStock based on trade type.
     */
    @Transactional
    public void executeTrade(TradeRequest tr) {
        BigDecimal price = BigDecimal.valueOf(tr.getPricePerUnit());
        Portfolio portfolio = tr.getPortfolio();

        if ("BUY".equalsIgnoreCase(tr.getType())) {
            buyStock(portfolio.getId(), tr.getStockSymbol(), tr.getQuantity(), price);
        } else if ("SELL".equalsIgnoreCase(tr.getType())) {
            sellStock(portfolio.getId(), tr.getStockSymbol(), tr.getQuantity(), price);
        } else {
            throw new RuntimeException("Unknown trade type: " + tr.getType());
        }
    }

    @Transactional
    public void buyStock(Long portfolioId, String stockSymbol, int quantity, BigDecimal pricePerUnit) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        BigDecimal totalCost = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

        if (portfolio.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient balance to buy stock");
        }

        portfolio.setBalance(portfolio.getBalance().subtract(totalCost));

        Stock stock = stockRepository.findBySymbol(stockSymbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockSymbol));

        PortfolioHolding holding = holdingRepository
                .findByPortfolioIdAndStock_Symbol(portfolioId, stockSymbol)
                .orElse(PortfolioHolding.builder()
                        .portfolio(portfolio)
                        .stock(stock)
                        .quantity(0)
                        .avgPrice(BigDecimal.ZERO)
                        .build());

        // Recalculate average price
        BigDecimal currentTotalValue = holding.getAvgPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
        BigDecimal newTotalValue = currentTotalValue.add(totalCost);
        int newQuantity = holding.getQuantity() + quantity;
        BigDecimal newAvgPrice = newTotalValue.divide(BigDecimal.valueOf(newQuantity), RoundingMode.HALF_UP);

        holding.setQuantity(newQuantity);
        holding.setAvgPrice(newAvgPrice);

        holdingRepository.save(holding);
        portfolioRepository.save(portfolio);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setUser(portfolio.getUser());
        transaction.setStock(stock);
        transaction.setType("BUY");
        transaction.setQuantity(quantity);
        transaction.setPrice(pricePerUnit.doubleValue());
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void sellStock(Long portfolioId, String stockSymbol, int quantity, BigDecimal pricePerUnit) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        PortfolioHolding holding = holdingRepository
                .findByPortfolioIdAndStock_Symbol(portfolioId, stockSymbol)
                .orElseThrow(() -> new RuntimeException("Stock not found in portfolio"));

        if (holding.getQuantity() < quantity) {
            throw new RuntimeException("Not enough shares to sell");
        }

        if (pricePerUnit == null) {
            Stock stock = stockRepository.findBySymbol(stockSymbol)
                    .orElseThrow(() -> new RuntimeException("Stock not found: " + stockSymbol));
            pricePerUnit = BigDecimal.valueOf(stock.getCurrentPrice());
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

        // Save transaction
        Stock stock = stockRepository.findBySymbol(stockSymbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + stockSymbol));

        Transaction transaction = new Transaction();
        transaction.setUser(portfolio.getUser());
        transaction.setStock(stock);
        transaction.setType("SELL");
        transaction.setQuantity(quantity);
        transaction.setPrice(pricePerUnit.doubleValue());
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    /**
     * Validates a BUY order before it is executed.
     * Used for Input Space Partitioning
     */
    public boolean isValidBuyOrder(int quantity, double pricePerUnit, BigDecimal balance) {
        if (quantity <= 0) return false;
        if (pricePerUnit <= 0) return false;
        BigDecimal totalCost = BigDecimal.valueOf(pricePerUnit).multiply(BigDecimal.valueOf(quantity));
        return balance.compareTo(totalCost) >= 0;
    }

    /**
     * Decides whether a trade can proceed given stock availability,
     * with an admin-override escape hatch for stocks not yet synced
     * from the scraper. Used for Logic Coverage
     */
    public boolean isTradeExecutable(int quantity, boolean stockExists, boolean adminOverride) {
        boolean validQuantity = quantity > 0;
        return validQuantity && (stockExists || adminOverride);
    }
}