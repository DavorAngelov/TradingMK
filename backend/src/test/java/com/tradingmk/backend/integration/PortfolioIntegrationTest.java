package com.tradingmk.backend.integration;

import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
class PortfolioIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tradingmk_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired private UserRepository userRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private com.tradingmk.backend.service.PortfolioService portfolioService;

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        User user = User.builder().username("itest_user").password("x").email("i@test.com")
                .role(Role.USER).build();
        userRepository.save(user);

        Portfolio p = new Portfolio();
        p.setUser(user);
        p.setBalance(new BigDecimal("5000.00"));
        portfolio = portfolioRepository.save(p);

        Stock stock = new Stock();
        stock.setSymbol("KMB");
        stock.setName("Komercijalna Banka");
        stock.setCurrentPrice(100.0);
        stockRepository.save(stock);
    }

    @Test
    void buyStock_persistsToRealPostgres() {
        portfolioService.buyStock(portfolio.getId(), "KMB", 10, new BigDecimal("100.00"));

        Portfolio updated = portfolioRepository.findById(portfolio.getId()).orElseThrow();
        assertEquals(new BigDecimal("4000.00"), updated.getBalance());
    }
}