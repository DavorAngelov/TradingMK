package com.tradingmk.backend.model;


import com.tradingmk.backend.service.UserService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "watchlist")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private Double priceAbove;
    private Double priceBelow;




}
