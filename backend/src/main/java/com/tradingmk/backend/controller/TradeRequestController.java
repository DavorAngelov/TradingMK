package com.tradingmk.backend.controller;


import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.*;
import com.tradingmk.backend.service.EmailService;
import com.tradingmk.backend.service.PortfolioService;
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
    private final PortfolioService portfolioService;
    private final EmailService emailService;

    public TradeRequestController(TradeRequestRepository tradeRequestRepository,
                                  PortfolioRepository portfolioRepository,
                                  PortfolioService portfolioService,
                                  EmailService emailService) {
        this.tradeRequestRepository = tradeRequestRepository;
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
        this.emailService = emailService;
    }

    @PostMapping("/request")
    public TradeRequest requestTrade(@RequestBody TradeRequest tradeRequest,
                                     @AuthenticationPrincipal User user) {
        tradeRequest.setStatus("PENDING");
        tradeRequest.setTimestamp(LocalDateTime.now());

        Portfolio portfolio = portfolioRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Portfolio not found for user"));

        tradeRequest.setPortfolio(portfolio);
        return tradeRequestRepository.save(tradeRequest);
    }

    @GetMapping("/pending")
    public List<TradeRequest> getPendingTrades() {
        return tradeRequestRepository.findByStatus("PENDING");
    }

    @PostMapping("/{id}/approve")
    public TradeRequest approveTrade(@PathVariable Long id) {
        TradeRequest tr = tradeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found"));

        if (!"PENDING".equals(tr.getStatus())) {
            throw new RuntimeException("Trade already processed!");
        }

        // All business logic delegated to service
        portfolioService.executeTrade(tr);

        tr.setStatus("APPROVED");
        TradeRequest saved = tradeRequestRepository.save(tr);

        // Email notification (non-critical, outside transaction)
        try {
            Portfolio portfolio = tr.getPortfolio();
            emailService.sendEmail(
                    portfolio.getUser().getEmail(),
                    "Trade Approved - " + tr.getStockSymbol(),
                    "Your request to " + tr.getType() + " " + tr.getQuantity() +
                            " shares of " + tr.getStockSymbol() + " has been approved."
            );
        } catch (Exception e) {
            System.err.println("Email failed but trade was approved: " + e.getMessage());
        }

        return saved;
    }

    @PostMapping("/{id}/decline")
    public TradeRequest declineTrade(@PathVariable Long id) {
        TradeRequest tr = tradeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade not found"));
        tr.setStatus("DECLINED");

        try {
            Portfolio portfolio = tr.getPortfolio();
            emailService.sendEmail(
                    portfolio.getUser().getEmail(),
                    "Trade Declines - " + tr.getStockSymbol(),
                    "Your request to " + tr.getType() + " " + tr.getQuantity() +
                            " shares of " + tr.getStockSymbol() + " has been declined."
            );
        } catch (Exception e) {
            System.err.println("Email failed but trade was approved: " + e.getMessage());
        }

        return tradeRequestRepository.save(tr);
    }
}