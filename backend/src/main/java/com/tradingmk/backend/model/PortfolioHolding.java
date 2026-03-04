package com.tradingmk.backend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tradingmk.backend.repository.PortfolioRepository;
import jakarta.persistence.*;
import lombok.*;

import javax.sound.sampled.Port;
import java.math.BigDecimal;

@Entity
@Table(name = "portfolio_holdings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioHolding  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)

    private Portfolio portfolio;

//    @Column(nullable = false)
//    private String stockSymbol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_id", nullable = false)
    @JsonBackReference
    private Stock stock;


    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal avgPrice;
}
