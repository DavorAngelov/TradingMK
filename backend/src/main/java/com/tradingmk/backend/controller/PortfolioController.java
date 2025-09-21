package com.tradingmk.backend.controller;


import com.tradingmk.backend.dto.*;
import com.tradingmk.backend.model.Portfolio;
import com.tradingmk.backend.model.PortfolioHolding;
import com.tradingmk.backend.repository.PortfolioHoldingRepository;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.UserRepository;
import com.tradingmk.backend.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioService portfolioService;



    public PortfolioController(PortfolioRepository portfolioRepository, UserRepository userRepository, PortfolioHoldingRepository portfolioHoldingRepository, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.portfolioHoldingRepository = portfolioHoldingRepository;
        this.portfolioService = portfolioService;
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

        return ResponseEntity.ok(portfolioDTO);
    }

//    @PostMapping("/buy")
//    public ResponseEntity<String> buyStock(@RequestBody BuyStockRequest request, Principal principal) {
//        System.out.println("Principal: " + principal);
//        //geting user
//        var user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new RuntimeException("user not found"));
//
//        //portfolio gett
//        var portfolio = portfolioRepository.findByUserId(user.getId())
//                .orElseThrow(() -> new RuntimeException("portfolio not found"));
//
//        portfolioService.buyStock(
//                portfolio.getId(),
//                request.getStockSymbol(),
//                request.getQuantity(),
//                request.getPricePerUnit()
//        );
//
//        return ResponseEntity.ok("stock purchased successfuly");
//    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody SellStockRequest request, Principal principal) {
        //geting user
        var user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("user not found"));


        //portfolio gett
        var portfolio = portfolioRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("portfolio not found"));

        portfolioService.sellStock(
                portfolio.getId(),
                request.getStockSymbol(),
                request.getQuantity(),
                request.getPricePerUnit()
        );

        return ResponseEntity.ok("stock sold successfuly");
    }

}
