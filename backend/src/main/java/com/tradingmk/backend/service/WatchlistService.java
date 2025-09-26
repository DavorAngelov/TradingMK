package com.tradingmk.backend.service;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.repository.UserRepository;
import com.tradingmk.backend.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;

    public List<WatchlistEntry> getUserWatchlist(User user){
        return watchlistRepository.findByUser(user);
    }

    public WatchlistEntry addToWatchlist(User user, Stock stock, Double priceAbove, Double priceBelow) {
        WatchlistEntry w = new WatchlistEntry();
        w.setUser(user);
        w.setStock(stock);
        w.setPriceAbove(priceAbove);
        w.setPriceBelow(priceBelow);
        return watchlistRepository.save(w);
    }

    public void removeFromWatchlist(Long id){
        watchlistRepository.deleteById(id);
    }
}
