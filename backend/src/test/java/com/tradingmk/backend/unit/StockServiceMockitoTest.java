package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.repository.StockRepository;
import com.tradingmk.backend.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceMockitoTest {

    @Mock private StockRepository stockRepository;

    private StockService stockService;

    @BeforeEach
    void setUp() throws Exception {
        stockService = new StockService();
        Field field = StockService.class.getDeclaredField("stockRepository");
        field.setAccessible(true);
        field.set(stockService, stockRepository);
    }

    @Test
    void getAllStocks_returnsAllFromRepository() {
        Stock s1 = new Stock();
        s1.setSymbol("KMB");
        Stock s2 = new Stock();
        s2.setSymbol("ALK");

        when(stockRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Stock> result = stockService.getAllStocks();

        assertEquals(2, result.size());
        assertTrue(result.contains(s1));
        assertTrue(result.contains(s2));
    }

    @Test
    void getBySymbol_found_returnsStock() {
        Stock stock = new Stock();
        stock.setSymbol("KMB");
        stock.setCurrentPrice(120.0);

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        Stock result = stockService.getBySymbol("KMB");

        assertEquals("KMB", result.getSymbol());
        assertEquals(120.0, result.getCurrentPrice());
    }

    @Test
    void getBySymbol_notFound_throwsWithCorrectMessage() {
        when(stockRepository.findBySymbol("XXX")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                stockService.getBySymbol("XXX"));

        assertEquals("Stock not found with symbol: XXX", ex.getMessage());
    }

    @Test
    void updateStockPrices_existingStock_updatesAllFields() {
        Stock existing = new Stock();
        existing.setSymbol("KMB");
        existing.setName("Old Name");
        existing.setCurrentPrice(100.0);
        existing.setLastPrice(95.0);
        existing.setPercentage(1.5);
        existing.setTurnover(1000.0);

        Stock incoming = new Stock();
        incoming.setSymbol("KMB");
        incoming.setName("Komercijalna Banka");
        incoming.setCurrentPrice(110.0);
        incoming.setLastPrice(105.0);
        incoming.setPercentage(2.5);
        incoming.setTurnover(2000.0);

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(existing));

        stockService.updateStockPrices(List.of(incoming));

        ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository).save(captor.capture());
        Stock saved = captor.getValue();

        assertEquals("Komercijalna Banka", saved.getName());
        assertEquals(110.0, saved.getCurrentPrice());
        assertEquals(105.0, saved.getLastPrice());
        assertEquals(2.5, saved.getPercentage());
        assertEquals(2000.0, saved.getTurnover());
        assertNotNull(saved.getLastUpdated());
    }

    @Test
    void updateStockPrices_existingStock_nullPercentage_defaultsToZero() {
        Stock existing = new Stock();
        existing.setSymbol("KMB");

        Stock incoming = new Stock();
        incoming.setSymbol("KMB");
        incoming.setName("Komercijalna Banka");
        incoming.setPercentage(null);

        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(existing));

        stockService.updateStockPrices(List.of(incoming));

        ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository).save(captor.capture());

        assertEquals(0.0, captor.getValue().getPercentage());
    }

    @Test
    void updateStockPrices_newStock_savesIncomingDirectly() {
        Stock incoming = new Stock();
        incoming.setSymbol("NEWSTK");
        incoming.setName("New Stock");
        incoming.setCurrentPrice(50.0);

        when(stockRepository.findBySymbol("NEWSTK")).thenReturn(Optional.empty());

        stockService.updateStockPrices(List.of(incoming));

        ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
        verify(stockRepository).save(captor.capture());
        assertEquals("NEWSTK", captor.getValue().getSymbol());
        assertNotNull(captor.getValue().getLastUpdated());
    }
}