package com.tradingmk.backend.controller;


import com.tradingmk.backend.dto.PortfolioDTO;
import com.tradingmk.backend.dto.PortfolioHoldingDTO;
import com.tradingmk.backend.dto.PortfolioResponse;
import com.tradingmk.backend.model.PortfolioHolding;
import com.tradingmk.backend.repository.PortfolioHoldingRepository;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;

    public PortfolioController(PortfolioRepository portfolioRepository, UserRepository userRepository, PortfolioHoldingRepository portfolioHoldingRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.portfolioHoldingRepository = portfolioHoldingRepository;
    }

    @GetMapping
    public ResponseEntity<?> getMyPortfolio(@AuthenticationPrincipal UserDetails userDetails) {
        var user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("user not found"));

        var portfolio = portfolioRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("portfolio not found"));


        List<PortfolioHoldingDTO> holdings = portfolioHoldingRepository.findByPortfolioId(portfolio.getId())
                .stream()
                .map(holding -> new PortfolioHoldingDTO(
                        holding.getStockSymbol(),
                        holding.getQuantity(),
                        holding.getAvgPrice()
                ))
                .collect(Collectors.toList());

        PortfolioDTO portfolioDTO = new PortfolioDTO(portfolio.getBalance(), holdings);

        return ResponseEntity.ok(portfolio);
    }
}
