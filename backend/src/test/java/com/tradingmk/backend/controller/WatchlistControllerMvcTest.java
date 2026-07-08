package com.tradingmk.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingmk.backend.dto.WatchlistRequest;
import com.tradingmk.backend.model.*;
import com.tradingmk.backend.service.StockService;
import com.tradingmk.backend.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tradingmk.backend.config.JwtAuthenticationFilter;
import com.tradingmk.backend.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Spring MVC Test Framework (MockMvc) tests for WatchlistController.
 * @WebMvcTest loads only the web layer — no real database, no real service beans.
 * Security filters are disabled here so we can inject a fake authenticated user
 * directly via @AuthenticationPrincipal, without needing a real JWT.
 */
@WebMvcTest(WatchlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WatchlistControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WatchlistService watchlistService;

    @MockBean
    private StockService stockService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private Stock testStock;

    private RequestPostProcessor authentication(User user) {
        return request -> {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        };
    }
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("davor")
                .password("davor")
                .role(Role.USER)
                .build();

        testStock = new Stock();
        testStock.setId(1L);
        testStock.setSymbol("KMB");
        testStock.setCurrentPrice(120.0);
    }

    @Test
    void getWatchlist_returnsUsersEntries_asJsonArray() throws Exception {
        WatchlistEntry entry = new WatchlistEntry();
        entry.setId(10L);
        entry.setStock(testStock);
        entry.setPriceAbove(500.0);

        when(watchlistService.getUserWatchlist(any(User.class)))
                .thenReturn(List.of(entry));

        mockMvc.perform(get("/api/watchlist")
                        .with(authentication(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].priceAbove").value(500.0));

        verify(watchlistService).getUserWatchlist(any(User.class));
    }

    @Test
    void getWatchlist_emptyList_returnsEmptyJsonArray() throws Exception {
        when(watchlistService.getUserWatchlist(any(User.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/watchlist").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addToWatchlist_validRequest_returnsCreatedEntry() throws Exception {
        WatchlistRequest request = new WatchlistRequest();
        request.setSymbol("KMB");
        request.setPriceAbove(500.0);
        request.setPriceBelow(100.0);

        WatchlistEntry saved = new WatchlistEntry();
        saved.setId(99L);
        saved.setStock(testStock);
        saved.setPriceAbove(500.0);
        saved.setPriceBelow(100.0);

        when(stockService.getBySymbol("KMB")).thenReturn(testStock);
        when(watchlistService.addToWatchlist(any(User.class), eq(testStock), eq(500.0), eq(100.0)))
                .thenReturn(saved);

        mockMvc.perform(post("/api/watchlist")
                        .with(authentication(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.priceAbove").value(500.0))
                .andExpect(jsonPath("$.priceBelow").value(100.0));

        verify(stockService).getBySymbol("KMB");
        verify(watchlistService).addToWatchlist(
                any(User.class),
                eq(testStock),
                eq(500.0),
                eq(100.0)
        );
    }

    @Test
    void deleteWatchlistEntry_validId_returns200() throws Exception {
        mockMvc.perform(delete("/api/watchlist/{id}", 10L).with(user(testUser)))
                .andExpect(status().isOk());

        verify(watchlistService).removeFromWatchlist(10L);
    }

    @Test
    void addToWatchlist_missingBody_returns400() throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }
}