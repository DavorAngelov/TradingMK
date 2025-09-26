package com.tradingmk.backend;

import com.tradingmk.backend.model.Stock;
import com.tradingmk.backend.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public CommandLineRunner demo(StockRepository repo) {
//        return args -> {
//            Stock s = new Stock();
//            s.setSymbol("ALK");
//            s.setCurrentPrice(1200.0);
//            s.setName("Alkaloid");
//            repo.save(s);
//        };
//    }





}
