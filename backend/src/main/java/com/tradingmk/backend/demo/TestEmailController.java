package com.tradingmk.backend.demo;
import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.model.WatchlistEntry;
import com.tradingmk.backend.service.WatchlistAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final WatchlistAlertService alertService;

    @GetMapping("/send-test-alert")
    public String sendTestAlert() {

        User user = new User();
        user.setEmail("");

        Stock stock = new Stock();
        stock.setSymbol("MPT");
        stock.setCurrentPrice(120.5);


        WatchlistEntry entry = new WatchlistEntry();
        entry.setUser(user);
        entry.setStock(stock);
        entry.setPriceBelow(150.0);

        alertService.sendEmail(entry, "TEST ALERT: Stock " + stock.getSymbol() + " is now " + stock.getCurrentPrice());

        return "Test alert sent to " + user.getEmail();
    }
}