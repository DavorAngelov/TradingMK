package com.tradingmk.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingmk.backend.config.JwtAuthenticationFilter;
import com.tradingmk.backend.model.*;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.TradeRequestRepository;
import com.tradingmk.backend.service.EmailService;
import com.tradingmk.backend.service.PortfolioService;
import com.tradingmk.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TradeRequestControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TradeRequestRepository tradeRequestRepository;

    @MockBean
    private PortfolioRepository portfolioRepository;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private EmailService emailService;

    // Security dependencies
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    private User testUser;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("davor")
                .password("davor")
                .email("trader1@test.com")
                .role(Role.USER)
                .build();

        portfolio = new Portfolio();
        portfolio.setId(5L);
        portfolio.setUser(testUser);
    }

    private RequestPostProcessor authentication(User user) {
        return request -> {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            return request;
        };
    }

    @Test
    void requestTrade_validPayload_savesAsPending() throws Exception {
        TradeRequest incoming = new TradeRequest();
        incoming.setStockSymbol("KMB");
        incoming.setQuantity(10);
        incoming.setPricePerUnit(100.0);
        incoming.setType("BUY");

        when(portfolioRepository.findByUserId(1L))
                .thenReturn(Optional.of(portfolio));

        when(tradeRequestRepository.save(any(TradeRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/trades/request")
                        .with(authentication(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.stockSymbol").value("KMB"));
    }

    @Test
    void requestTrade_noPortfolioForUser_throwsException() throws Exception {
        TradeRequest incoming = new TradeRequest();
        incoming.setStockSymbol("KMB");
        incoming.setQuantity(10);
        incoming.setPricePerUnit(100.0);
        incoming.setType("BUY");

        when(portfolioRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/trades/request")
                        .with(authentication(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incoming)))
        );

        verify(tradeRequestRepository, never()).save(any(TradeRequest.class));
    }

    @Test
    void getPendingTrades_returnsOnlyPendingOnes() throws Exception {
        TradeRequest t1 = new TradeRequest();
        t1.setId(1L);
        t1.setStatus("PENDING");
        t1.setStockSymbol("KMB");

        when(tradeRequestRepository.findByStatus("PENDING"))
                .thenReturn(List.of(t1));

        mockMvc.perform(get("/api/trades/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void approveTrade_pendingTrade_executesAndReturnsApproved() throws Exception {
        TradeRequest tr = new TradeRequest();
        tr.setId(1L);
        tr.setStatus("PENDING");
        tr.setStockSymbol("KMB");
        tr.setQuantity(5);
        tr.setType("BUY");
        tr.setPortfolio(portfolio);

        when(tradeRequestRepository.findById(1L))
                .thenReturn(Optional.of(tr));

        when(tradeRequestRepository.save(any(TradeRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        doNothing()
                .when(portfolioService)
                .executeTrade(any(TradeRequest.class));

        doNothing()
                .when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/trades/{id}/approve", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(portfolioService).executeTrade(tr);
    }

    @Test
    void approveTrade_alreadyProcessed_throwsExceptionAndDoesNotExecute() throws Exception {
        TradeRequest tr = new TradeRequest();
        tr.setId(2L);
        tr.setStatus("APPROVED");

        when(tradeRequestRepository.findById(2L))
                .thenReturn(Optional.of(tr));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/trades/{id}/approve", 2L))
        );

        verify(portfolioService, never())
                .executeTrade(any());
    }

    @Test
    void approveTrade_tradeIdNotFound_throwsException() throws Exception {
        when(tradeRequestRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/trades/{id}/approve", 999L))
        );
    }

    @Test
    void declineTrade_pendingTrade_setsStatusDeclined() throws Exception {
        TradeRequest tr = new TradeRequest();
        tr.setId(3L);
        tr.setStatus("PENDING");
        tr.setStockSymbol("KMB");
        tr.setType("BUY");
        tr.setPortfolio(portfolio);

        when(tradeRequestRepository.findById(3L))
                .thenReturn(Optional.of(tr));

        when(tradeRequestRepository.save(any(TradeRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        doNothing()
                .when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/trades/{id}/decline", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"));

        verify(portfolioService, never())
                .executeTrade(any());
    }
}