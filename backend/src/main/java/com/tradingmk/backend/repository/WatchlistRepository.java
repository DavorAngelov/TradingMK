package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchlistRepository extends JpaRepository<WatchlistEntry,Long> {
    List<WatchlistEntry> findByUser(User user);
}
