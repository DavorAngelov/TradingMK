package com.tradingmk.backend.repository;

import com.tradingmk.backend.model.PendingLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingLinkRepository extends JpaRepository<PendingLink, String> {
    Optional<PendingLink> findByToken(String token);
    void deleteByToken(String token);
}