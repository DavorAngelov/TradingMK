package com.tradingmk.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "oauth_pending_links")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingLink {
    @Id
    private String token;

    private String email;

    private String provider;

    private Instant expiresAt;

    private Instant createdAt;
}
