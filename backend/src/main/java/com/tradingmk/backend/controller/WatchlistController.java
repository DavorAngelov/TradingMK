package com.tradingmk.backend.controller;


import com.tradingmk.backend.dto.WatchlistRequest;
import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.service.StockService;
import com.tradingmk.backend.service.UserService;
import com.tradingmk.backend.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;
    private final StockService stockService;

    @GetMapping
    public List<WatchlistEntry> getWatchlist(@AuthenticationPrincipal User user) {
        return watchlistService.getUserWatchlist(user);
    }

    @PostMapping
    public WatchlistEntry addToWatchlist(@RequestBody WatchlistRequest request,
                                         @AuthenticationPrincipal User user) {
        Stock stock = stockService.getBySymbol(request.getSymbol());
        return watchlistService.addToWatchlist(user, stock, request.getPriceAbove(), request.getPriceBelow());
    }

    @DeleteMapping("/{id}")
    public void removeFromWatchlist(@PathVariable Long id) {
        watchlistService.removeFromWatchlist(id);
    }
}
