package com.tradingmk.backend.controller;


import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trades")
public class TradeRequestController {

    private final TradeRequestRepository tradeRequestRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;

    private final TransactionRepository transactionRepository;
    private final StockRepository stockRepository;

    public TradeRequestController(TradeRequestRepository tradeRequestRepository, PortfolioRepository portfolioRepository, PortfolioHoldingRepository portfolioHoldingRepository, TransactionRepository transactionRepository, StockRepository stockRepository) {
        this.tradeRequestRepository = tradeRequestRepository;
        this.portfolioRepository = portfolioRepository;
        this.portfolioHoldingRepository = portfolioHoldingRepository;
        this.transactionRepository = transactionRepository;
        this.stockRepository = stockRepository;
    }

    // sending the trade request
    @PostMapping("/request")
    public TradeRequest requestTrade(@RequestBody TradeRequest tradeRequest, @AuthenticationPrincipal User user) {
        tradeRequest.setStatus("PENDING");
        tradeRequest.setTimestamp(LocalDateTime.now());

        // fetch the portfolio for this user
        Portfolio portfolio = portfolioRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        tradeRequest.setPortfolioId(portfolio.getId());
        System.out.println("Portfolio ID set: " + portfolio.getId());

        return tradeRequestRepository.save(tradeRequest);
    }

    // admin views the pending requests
    @GetMapping("/pending")
    public List<TradeRequest> getPendingTrades() {
        return tradeRequestRepository.findByStatus("PENDING");
    }

    // admin can approve or declinee
    @PostMapping("/{id}/approve")
    public TradeRequest approveTrade(@PathVariable Long id) {
        TradeRequest tr = tradeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        if (!"PENDING".equals(tr.getStatus())) {
            throw new RuntimeException("Trade already processed!");
        }

        // Load portfolio from tradeRequest
        Portfolio portfolio = portfolioRepository.findById(tr.getPortfolioId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if ("BUY".equalsIgnoreCase(tr.getType())) {
            BigDecimal totalCost = BigDecimal.valueOf(tr.getQuantity() * tr.getPricePerUnit());
//            if (portfolio.getBalance().compareTo(totalCost) < 0) {
//                throw new RuntimeException("Insufficient balance in portfolio");
//            }
            //TODO GET THIS BACK
            portfolio.setBalance(portfolio.getBalance().subtract(totalCost));
            portfolioRepository.save(portfolio);

            PortfolioHolding holding = portfolioHoldingRepository
                    .findByPortfolioIdAndStockSymbol(portfolio.getId(), tr.getStockSymbol())
                    .orElseGet(() -> {
                        PortfolioHolding newHolding = new PortfolioHolding();
                        newHolding.setPortfolio(portfolio);
                        newHolding.setStockSymbol(tr.getStockSymbol());
                        newHolding.setQuantity(0);
                        newHolding.setAvgPrice(BigDecimal.ZERO);
                        return newHolding;
                    });

            BigDecimal oldCost = holding.getAvgPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
            BigDecimal newCost = BigDecimal.valueOf(tr.getPricePerUnit() * tr.getQuantity());
            int newQuantity = holding.getQuantity() + tr.getQuantity();

            holding.setQuantity(newQuantity);
            holding.setAvgPrice(newQuantity > 0 ? (oldCost.add(newCost)).divide(BigDecimal.valueOf(newQuantity), RoundingMode.HALF_UP) : BigDecimal.ZERO);

            portfolioHoldingRepository.save(holding);

            //sava a transaction
            Transaction transaction = new Transaction();
            transaction.setUser(portfolio.getUser());

            Stock stock = stockRepository.findBySymbol(tr.getStockSymbol())
                    .orElseThrow(() -> new RuntimeException("stock not found: " + tr.getStockSymbol()));
            transaction.setStock(stock);
            transaction.setType("BUY");
            transaction.setQuantity(tr.getQuantity());
            transaction.setPrice(tr.getPricePerUnit());
            transaction.setTimestamp(LocalDateTime.now());

            transactionRepository.save(transaction);

        } else if ("SELL".equalsIgnoreCase(tr.getType())) {
            PortfolioHolding holding = portfolioHoldingRepository
                    .findByPortfolioIdAndStockSymbol(portfolio.getId(), tr.getStockSymbol())
                    .orElseThrow(() -> new RuntimeException("No holdings for stock " + tr.getStockSymbol()));

            if (holding.getQuantity() < tr.getQuantity()) {
                throw new RuntimeException("Not enough stock quantity to sell");
            }


            holding.setQuantity(holding.getQuantity() - tr.getQuantity());


            if (holding.getQuantity() <= 0) {
                portfolioHoldingRepository.delete(holding);
            } else {
                portfolioHoldingRepository.save(holding);
            }


            BigDecimal totalGain = BigDecimal.valueOf(tr.getQuantity() * tr.getPricePerUnit());
            portfolio.setBalance(portfolio.getBalance().add(totalGain));
            portfolioRepository.save(portfolio);


            //sava a transaction
            Transaction transaction = new Transaction();
            transaction.setUser(portfolio.getUser());

            Stock stock = stockRepository.findBySymbol(tr.getStockSymbol())
                    .orElseThrow(() -> new RuntimeException("stock not found: " + tr.getStockSymbol()));
            transaction.setStock(stock);
            transaction.setType("BUY");
            transaction.setQuantity(tr.getQuantity());
            transaction.setPrice(tr.getPricePerUnit());
            transaction.setTimestamp(LocalDateTime.now());

            transactionRepository.save(transaction);
        }

        tr.setStatus("APPROVED");
        return tradeRequestRepository.save(tr);
    }

    @PostMapping("/{id}/decline")
    public TradeRequest declineTrade(@PathVariable Long id) {
        TradeRequest tr = tradeRequestRepository.findById(id).orElseThrow();
        tr.setStatus("DECLINED");
        return tradeRequestRepository.save(tr);
    }
}
