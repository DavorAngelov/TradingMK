package com.tradingmk.backend.unit;


import com.tradingmk.backend.repository.*;
import com.tradingmk.backend.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PortfolioServiceLogicCoverageTest {

    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(
                mock(PortfolioRepository.class),
                mock(PortfolioHoldingRepository.class),
                mock(TransactionRepository.class),
                mock(StockRepository.class)
        );
    }

    // quantity, stockExists, adminOverride, expected -- RACC test set
    @ParameterizedTest(name = "T{index}: qty={0}, stockExists={1}, override={2} -> {3}")
    @CsvSource({
            "5,  true,  false, true",   // T1
            "-5, true,  false, false",  // T2 (A active vs T1)
            "5,  false, false, false",  // T3 (B active vs T1)
            "5,  false, true,  true"    // T4 (C active vs T3)
    })
    void testIsTradeExecutable_RACC(int quantity, boolean stockExists, boolean adminOverride, boolean expected) {
        assertEquals(expected, portfolioService.isTradeExecutable(quantity, stockExists, adminOverride));
    }
}