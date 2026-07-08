package com.tradingmk.backend.unit;

import com.tradingmk.backend.service.StockHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class StockHistoryServiceGraphTest {

    private StockHistoryService service;

    @BeforeEach
    void setUp() {
        service = new StockHistoryService();
    }

    @Test
    void noLoopExecution_returnsStable() {
        // Prime path: N1-N2-N9
        assertEquals("STABLE", service.analyzeTrend(List.of(100.0)));
    }

    @Test
    void singleIncrease_returnsUp() {
        // Prime path: N1-N2-N3-N4-N5-N8-N2-N9
        assertEquals("UP", service.analyzeTrend(List.of(100.0, 110.0)));
    }

    @Test
    void singleDecrease_returnsDown() {
        // Prime path: N1-N2-N3-N4-N6-N7-N8-N2-N9
        assertEquals("DOWN", service.analyzeTrend(List.of(100.0, 90.0)));
    }

    @Test
    void noChange_returnsStable() {
        // Prime path: N1-N2-N3-N4-N6-N8-N2-N9
        assertEquals("STABLE", service.analyzeTrend(List.of(100.0, 100.0)));
    }

    @Test
    void twoIterations_lastMovementWins() {
        // All-DU-Paths: def@N3(iter1)->use@N4(iter1), def@N3(iter2)->use@N6(iter2)
        assertEquals("DOWN", service.analyzeTrend(List.of(100.0, 110.0, 105.0)));
    }

    @Test
    void emptyList_returnsStable() {
        assertEquals("STABLE", service.analyzeTrend(List.of()));
    }
}