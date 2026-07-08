package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.repository.WatchlistRepository;
import com.tradingmk.backend.service.WatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceMockitoTest {

    @Mock private WatchlistRepository watchlistRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    private User user;
    private Stock stock;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("trader1").password("x")
                .email("trader1@test.com").role(Role.USER).build();

        stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("KMB");
    }

    @Test
    void getUserWatchlist_returnsEntriesForUser() {
        WatchlistEntry entry = WatchlistEntry.builder().id(1L).user(user).stock(stock).build();
        when(watchlistRepository.findByUser(user)).thenReturn(List.of(entry));

        List<WatchlistEntry> result = watchlistService.getUserWatchlist(user);

        assertEquals(1, result.size());
        assertEquals(entry, result.get(0));
    }

    @Test
    void addToWatchlist_buildsEntryWithAllFieldsAndSaves() {
        WatchlistEntry saved = WatchlistEntry.builder()
                .id(10L).user(user).stock(stock).priceAbove(500.0).priceBelow(100.0).build();
        when(watchlistRepository.save(any(WatchlistEntry.class))).thenReturn(saved);

        WatchlistEntry result = watchlistService.addToWatchlist(user, stock, 500.0, 100.0);

        ArgumentCaptor<WatchlistEntry> captor = ArgumentCaptor.forClass(WatchlistEntry.class);
        verify(watchlistRepository).save(captor.capture());
        WatchlistEntry passed = captor.getValue();

        assertEquals(user, passed.getUser());
        assertEquals(stock, passed.getStock());
        assertEquals(500.0, passed.getPriceAbove());
        assertEquals(100.0, passed.getPriceBelow());
        assertEquals(saved, result);
    }

    @Test
    void addToWatchlist_nullPriceAlerts_stillSaves() {
        WatchlistEntry saved = WatchlistEntry.builder().id(11L).user(user).stock(stock).build();
        when(watchlistRepository.save(any(WatchlistEntry.class))).thenReturn(saved);

        watchlistService.addToWatchlist(user, stock, null, null);

        ArgumentCaptor<WatchlistEntry> captor = ArgumentCaptor.forClass(WatchlistEntry.class);
        verify(watchlistRepository).save(captor.capture());
        assertNull(captor.getValue().getPriceAbove());
        assertNull(captor.getValue().getPriceBelow());
    }

    @Test
    void removeFromWatchlist_deletesById() {
        watchlistService.removeFromWatchlist(42L);

        verify(watchlistRepository).deleteById(42L);
    }
}