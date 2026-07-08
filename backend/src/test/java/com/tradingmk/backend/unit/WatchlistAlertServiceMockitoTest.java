package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.repository.WatchlistRepository;
import com.tradingmk.backend.service.WatchlistAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistAlertServiceMockitoTest {

    @Mock private WatchlistRepository repository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private WatchlistAlertService watchlistAlertService;

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

    private WatchlistEntry entry(Double priceAbove, Double priceBelow, double currentPrice) {
        stock.setCurrentPrice(currentPrice);
        return WatchlistEntry.builder()
                .id(1L).user(user).stock(stock)
                .priceAbove(priceAbove).priceBelow(priceBelow).build();
    }

    @Test
    void checkWatchlist_priceBelowThreshold_sendsBelowAlert() {
        WatchlistEntry e = entry(null, 100.0, 90.0); // currentPrice 90 < priceBelow 100
        when(repository.findAll()).thenReturn(List.of(e));

        watchlistAlertService.checkWatchlist();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertTrue(captor.getValue().getText().contains("Price below alert"));
    }

    @Test
    void checkWatchlist_priceAboveThreshold_sendsAboveAlert() {
        WatchlistEntry e = entry(80.0, null, 90.0); // currentPrice 90 > priceAbove 80
        when(repository.findAll()).thenReturn(List.of(e));

        watchlistAlertService.checkWatchlist();

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        assertTrue(captor.getValue().getText().contains("Price above alert"));
    }

    @Test
    void checkWatchlist_priceWithinBounds_sendsNoAlert() {
        WatchlistEntry e = entry(200.0, 50.0, 90.0); // 50 < 90 < 200, no threshold crossed
        when(repository.findAll()).thenReturn(List.of(e));

        watchlistAlertService.checkWatchlist();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void checkWatchlist_nullThresholds_sendsNoAlert() {
        WatchlistEntry e = entry(null, null, 90.0);
        when(repository.findAll()).thenReturn(List.of(e));

        watchlistAlertService.checkWatchlist();

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void checkWatchlist_multipleEntries_onlyTriggeredOnesSendEmail() {
        WatchlistEntry triggered = entry(null, 100.0, 90.0);
        Stock stock2 = new Stock();
        stock2.setSymbol("ALK");
        stock2.setCurrentPrice(50.0);
        WatchlistEntry notTriggered = WatchlistEntry.builder()
                .id(2L).user(user).stock(stock2).priceAbove(200.0).priceBelow(10.0).build();

        when(repository.findAll()).thenReturn(List.of(triggered, notTriggered));

        watchlistAlertService.checkWatchlist();

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_buildsMessageWithCorrectFields() {
        WatchlistEntry e = entry(null, 100.0, 90.0);

        watchlistAlertService.sendEmail(e, "Custom alert message");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertEquals("tradingmkalerts@gmail.com", sent.getFrom());
        assertEquals("trader1@test.com", sent.getTo()[0]);
        assertEquals("Stock Alert: KMB", sent.getSubject());
        assertEquals("Custom alert message", sent.getText());
    }
}