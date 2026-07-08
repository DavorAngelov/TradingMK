package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.*;
import com.tradingmk.backend.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceMockitoTest {

    @Mock private PortfolioRepository portfolioRepository;
    @Mock private PortfolioHoldingRepository holdingRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private StockRepository stockRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio portfolio;
    private Stock stock;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@tradingmk.com");

        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setUser(user);
        portfolio.setBalance(new BigDecimal("10000.00"));

        stock = new Stock();
        stock.setId(1L);
        stock.setSymbol("KMB");
        stock.setCurrentPrice(100.0);
    }

    // ---------- buyStock ----------

    @Test
    void buyStock_success_deductsBalanceAndCreatesHolding() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.empty());

        portfolioService.buyStock(1L, "KMB", 10, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("9000.00"), portfolio.getBalance());
        verify(holdingRepository).save(any(PortfolioHolding.class));
        verify(portfolioRepository).save(portfolio);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void buyStock_insufficientBalance_throwsAndDoesNotSave() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                portfolioService.buyStock(1L, "KMB", 1000, new BigDecimal("100.00")));

        assertEquals("Insufficient balance to buy stock", ex.getMessage());
        verify(holdingRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void buyStock_balanceExactlyEqualsCost_succeeds() {
        portfolio.setBalance(new BigDecimal("1000.00"));
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.empty());

        portfolioService.buyStock(1L, "KMB", 10, new BigDecimal("100.00")); // 10*100 = 1000.00 exactly

        assertEquals(new BigDecimal("0.00"), portfolio.getBalance());
    }

    @Test
    void buyStock_portfolioNotFound_throws() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                portfolioService.buyStock(1L, "KMB", 10, new BigDecimal("100.00")));

        assertEquals("Portfolio not found", ex.getMessage());
    }

    @Test
    void buyStock_stockNotFound_throws() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("XXX")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                portfolioService.buyStock(1L, "XXX", 10, new BigDecimal("100.00")));

        assertEquals("Stock not found: XXX", ex.getMessage());
    }

    @Test
    void buyStock_existingHolding_recalculatesAveragePrice() {
        PortfolioHolding existing = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock)
                .quantity(10).avgPrice(new BigDecimal("100.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(existing));

        portfolioService.buyStock(1L, "KMB", 10, new BigDecimal("200.00"));

        ArgumentCaptor<PortfolioHolding> captor = ArgumentCaptor.forClass(PortfolioHolding.class);
        verify(holdingRepository).save(captor.capture());
        assertEquals(20, captor.getValue().getQuantity());
        assertEquals(new BigDecimal("150.00"), captor.getValue().getAvgPrice()); // (10*100 + 10*200)/20
    }

    @Test
    void buyStock_success_savesCorrectTransactionDetails() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.empty());

        portfolioService.buyStock(1L, "KMB", 10, new BigDecimal("100.00"));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(stock, saved.getStock());
        assertEquals("BUY", saved.getType());
        assertEquals(10, saved.getQuantity());
        assertEquals(100.0, saved.getPrice());
        assertNotNull(saved.getTimestamp());
    }

    // ---------- sellStock ----------

    @Test
    void sellStock_success_addsBalanceAndReducesHolding() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(10).avgPrice(new BigDecimal("90.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        portfolioService.sellStock(1L, "KMB", 5, new BigDecimal("100.00"));

        assertEquals(new BigDecimal("10500.00"), portfolio.getBalance());
        verify(holdingRepository).save(holding);
        assertEquals(5, holding.getQuantity());
    }

    @Test
    void sellStock_sellingEntirePosition_deletesHolding() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(5).avgPrice(new BigDecimal("90.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        portfolioService.sellStock(1L, "KMB", 5, new BigDecimal("100.00"));

        verify(holdingRepository).delete(holding);
        verify(holdingRepository, never()).save(any());
    }

    @Test
    void sellStock_notEnoughShares_throws() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(2).avgPrice(new BigDecimal("90.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                portfolioService.sellStock(1L, "KMB", 5, new BigDecimal("100.00")));
        assertEquals("Not enough shares to sell", ex.getMessage());
    }

    @Test
    void sellStock_holdingNotFound_throws() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                portfolioService.sellStock(1L, "KMB", 5, new BigDecimal("100.00")));

        assertEquals("Stock not found in portfolio", ex.getMessage());
    }

    @Test
    void sellStock_nullPrice_fallsBackToCurrentStockPrice() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(10).avgPrice(new BigDecimal("90.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock)); // currentPrice = 100.0

        portfolioService.sellStock(1L, "KMB", 5, null);

        assertEquals(new BigDecimal("10500.00"), portfolio.getBalance());
    }

    @Test
    void sellStock_success_savesCorrectTransactionDetails() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(10).avgPrice(new BigDecimal("90.00")).build();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        portfolioService.sellStock(1L, "KMB", 5, new BigDecimal("100.00"));

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(stock, saved.getStock());
        assertEquals("SELL", saved.getType());
        assertEquals(5, saved.getQuantity());
        assertEquals(100.0, saved.getPrice());
        assertNotNull(saved.getTimestamp());
    }

    // ---------- executeTrade dispatch ----------

    @Test
    void executeTrade_buyType_delegatesToBuyStock() {
        TradeRequest tr = new TradeRequest();
        tr.setType("BUY");
        tr.setPortfolio(portfolio);
        tr.setStockSymbol("KMB");
        tr.setQuantity(5);
        tr.setPricePerUnit(100.0);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.empty());

        portfolioService.executeTrade(tr);

        verify(transactionRepository).save(argThat(t -> t.getType().equals("BUY")));
    }

    @Test
    void executeTrade_sellType_delegatesToSellStock() {
        PortfolioHolding holding = PortfolioHolding.builder()
                .portfolio(portfolio).stock(stock).quantity(10).avgPrice(new BigDecimal("90.00")).build();

        TradeRequest tr = new TradeRequest();
        tr.setType("SELL");
        tr.setPortfolio(portfolio);
        tr.setStockSymbol("KMB");
        tr.setQuantity(5);
        tr.setPricePerUnit(100.0);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(holdingRepository.findByPortfolioIdAndStock_Symbol(1L, "KMB")).thenReturn(Optional.of(holding));
        when(stockRepository.findBySymbol("KMB")).thenReturn(Optional.of(stock));

        portfolioService.executeTrade(tr);

        verify(transactionRepository).save(argThat(t -> t.getType().equals("SELL")));
    }

    @Test
    void executeTrade_unknownType_throws() {
        TradeRequest tr = new TradeRequest();
        tr.setType("HOLD");
        tr.setPortfolio(portfolio);

        assertThrows(RuntimeException.class, () -> portfolioService.executeTrade(tr));
    }

    // ---------- isTradeExecutable boundary ----------

    @Test
    void isTradeExecutable_zeroQuantity_isFalse() {
        assertFalse(portfolioService.isTradeExecutable(0, true, false));
    }
}