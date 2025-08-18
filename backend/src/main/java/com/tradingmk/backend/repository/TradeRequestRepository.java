package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.TradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRequestRepository extends JpaRepository<TradeRequest,Long> {

    List<TradeRequest> findByStatus(String status);
}
