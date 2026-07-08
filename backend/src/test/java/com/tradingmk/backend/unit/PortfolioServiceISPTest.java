package com.tradingmk.backend.unit;


import com.tradingmk.backend.repository.*;
import com.tradingmk.backend.service.PortfolioService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
class PortfolioServiceISPTest {

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

    // quantity, pricePerUnit, balance, expected
    @ParameterizedTest(name = "Test #{index}: qty={0}, price={1}, balance={2} -> {3}")
    @CsvSource({
            "10, 100, 2000, true",   // 1 base: Q+, P+, F>
            "0,  100, 2000, false",  // 2: Q0
            "-5, 100, 2000, false",  // 3: Q-
            "10, 0,   2000, false",  // 4: P0
            "10, -1,  2000, false",  // 5: P-
            "10, 100, 1000, true",   // 6: F=
            "10, 100, 500,  false"   // 7: F
    })
    void testIsValidBuyOrder_BCC(int quantity, double pricePerUnit, String balance, boolean expected) {
        boolean result = portfolioService.isValidBuyOrder(quantity, pricePerUnit, new BigDecimal(balance));
        assertEquals(expected, result);
    }
}
