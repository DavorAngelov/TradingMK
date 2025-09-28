package com.tradingmk.backend.service;

import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistAlertService {

    private final WatchlistRepository repository;
    private final JavaMailSender mailSender;

    @Scheduled(fixedRate = 60000) //1minute
    public void checkWatchlist() {
        List<WatchlistEntry> entries = repository.findAll();

        for (WatchlistEntry entry : entries) {
            double currentPrice = entry.getStock().getCurrentPrice();

            if (entry.getPriceBelow() != null && currentPrice < entry.getPriceBelow()) {
                sendEmail(entry, "Price below alert! Current price: " + currentPrice);
            }

            if (entry.getPriceAbove() != null && currentPrice > entry.getPriceAbove()) {
                sendEmail(entry, "Price above alert! Current price: " + currentPrice);
            }
        }
    }

    public void sendEmail(WatchlistEntry entry, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("tradingmkalerts@gmail.com");
        mail.setTo(entry.getUser().getEmail());
        mail.setSubject("Stock Alert: " + entry.getStock().getSymbol());
        mail.setText(message);
        mailSender.send(mail);
    }
}
