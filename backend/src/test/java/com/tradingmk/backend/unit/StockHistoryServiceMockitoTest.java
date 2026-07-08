package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.StockHistory;
import com.tradingmk.backend.repository.StockHistoryRepository;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.service.StockHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito tests for StockHistoryService.saveAll and getHistoryForSymbol.
 * Complements StockHistoryServiceGraphTest, which already fully covers
 * analyzeTrend via graph coverage (All-DU-Paths / Prime Path / Edge-Pair).
 */
@ExtendWith(MockitoExtension.class)
class StockHistoryServiceMockitoTest {

    @Mock private StockHistoryRepository stockHistoryRepository;
    @Mock private StockRepository stockRepository;

    private StockHistoryService stockHistoryService;

    private Stock stock;

    @BeforeEach
    void setUp() throws Exception {
        stockHistoryService = new StockHistoryService();
        injectMock("stockHistoryRepository", stockHistoryRepository);
        injectMock("stockRepository", stockRepository);

        stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("KMB");
        stock.setCurrentPrice(120.0);
    }

    // StockHistoryService uses field injection (@Autowired), not constructor
    // injection, so we set the private fields directly via reflection here
    // rather than using @InjectMocks (which works fine either way, but this
    // keeps the intent explicit).
    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = StockHistoryService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(stockHistoryService, mock);
    }

    // ---------- saveAll ----------

    @Test
    void saveAll_validHistory_savesWithCorrectFields() {
        Stock incomingStockRef = new Stock();
        incomingStockRef.setSymbol("KMB");

        StockHistory incoming = new StockHistory();
        incoming.setStock(incomingStockRef);
        incoming.setPrice(150.0);
        incoming.setTimestamp(LocalDate.of(2026, 1, 15));

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        stockHistoryService.saveAll(List.of(incoming));

        ArgumentCaptor<StockHistory> captor = ArgumentCaptor.forClass(StockHistory.class);
        verify(stockHistoryRepository).save(captor.capture());
        StockHistory saved = captor.getValue();

        assertEquals(stock, saved.getStock());                          // kills line 34 mutant
        assertEquals(150.0, saved.getPrice());                          // kills line 35 mutant
        assertEquals(LocalDate.of(2026, 1, 15), saved.getTimestamp());  // kills line 36 mutant
    }

    @Test
    void saveAll_multipleEntries_savesEachOnce() {
        Stock ref = new Stock();
        ref.setSymbol("KMB");

        StockHistory h1 = new StockHistory();
        h1.setStock(ref);
        h1.setPrice(100.0);
        h1.setTimestamp(LocalDate.of(2026, 1, 1));

        StockHistory h2 = new StockHistory();
        h2.setStock(ref);
        h2.setPrice(110.0);
        h2.setTimestamp(LocalDate.of(2026, 1, 2));

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        stockHistoryService.saveAll(List.of(h1, h2));

        verify(stockHistoryRepository, times(2)).save(any(StockHistory.class));
    }

    @Test
    void saveAll_unknownSymbol_throwsWithCorrectMessage() {
        Stock ref = new Stock();
        ref.setSymbol("DOESNOTEXIST");

        StockHistory incoming = new StockHistory();
        incoming.setStock(ref);
        incoming.setPrice(100.0);
        incoming.setTimestamp(LocalDate.now());

        when(stockRepository.findBySymbol("DOESNOTEXIST")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                stockHistoryService.saveAll(List.of(incoming)));

        assertEquals("Stock not found: DOESNOTEXIST", ex.getMessage()); // kills line 31 mutant
        verify(stockHistoryRepository, never()).save(any());
    }

    @Test
    void saveAll_emptyList_savesNothing() {
        stockHistoryService.saveAll(List.of());

        verify(stockHistoryRepository, never()).save(any());
        verifyNoInteractions(stockRepository);
    }

    // ---------- getHistoryForSymbol ----------

    @Test
    void getHistoryForSymbol_validSymbol_returnsHistoryFromRepository() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        StockHistory h1 = new StockHistory();
        h1.setStock(stock);
        h1.setPrice(100.0);
        h1.setTimestamp(LocalDate.of(2026, 1, 10));

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(stockHistoryRepository.findByStockAndTimestampBetween(stock, from, to))
                .thenReturn(List.of(h1));

        List<StockHistory> result = stockHistoryService.getHistoryForSymbol("KMB", from, to);

        assertEquals(1, result.size());                 // kills line 47 mutant
        assertEquals(h1, result.get(0));
        verify(stockHistoryRepository).findByStockAndTimestampBetween(stock, from, to);
    }

    @Test
    void getHistoryForSymbol_unknownSymbol_throwsWithCorrectMessage() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        when(stockRepository.findBySymbol("XXX")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                stockHistoryService.getHistoryForSymbol("XXX", from, to));

        assertEquals("Stock not found: XXX", ex.getMessage()); // kills line 45 mutant
        verifyNoInteractions(stockHistoryRepository);
    }
}