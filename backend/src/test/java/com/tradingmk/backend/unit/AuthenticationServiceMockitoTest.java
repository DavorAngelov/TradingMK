package com.tradingmk.backend.unit;

import com.tradingmk.backend.dto.AuthenticationRequest;
import com.tradingmk.backend.dto.AuthenticationResponse;
import com.tradingmk.backend.dto.RegisterRequest;
import com.tradingmk.backend.model.Portfolio;
import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.UserRepository;
import com.tradingmk.backend.service.AuthenticationService;
import com.tradingmk.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceMockitoTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserService userService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PortfolioRepository portfolioRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_createsUserWithEncodedPasswordAndZeroBalancePortfolio() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newtrader")
                .email("newtrader@test.com")
                .password("plaintext")
                .build();

        when(passwordEncoder.encode("plaintext")).thenReturn("encoded-hash");
        when(userService.generateToken(anyMap(), any(User.class))).thenReturn("fake-jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertEquals("fake-jwt-token", response.getToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newtrader", savedUser.getUsername());
        assertEquals("newtrader@test.com", savedUser.getEmail());
        assertEquals("encoded-hash", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());

        ArgumentCaptor<Portfolio> portfolioCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        Portfolio savedPortfolio = portfolioCaptor.getValue();
        assertEquals(savedUser, savedPortfolio.getUser());
        assertEquals(BigDecimal.ZERO, savedPortfolio.getBalance());
    }

    @Test
    void register_passesCorrectClaimsToTokenGeneration() {
        RegisterRequest request = RegisterRequest.builder()
                .username("newtrader")
                .email("newtrader@test.com")
                .password("plaintext")
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-hash");
        when(userService.generateToken(anyMap(), any(User.class))).thenReturn("fake-jwt-token");

        authenticationService.register(request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(userService).generateToken(claimsCaptor.capture(), any(User.class));

        Map<String, Object> claims = claimsCaptor.getValue();
        assertEquals("newtrader@test.com", claims.get("email"));
        assertEquals("USER", claims.get("role"));
    }

    @Test
    void authenticate_validCredentials_returnsToken() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("trader1")
                .password("correct-password")
                .build();

        User existingUser = User.builder()
                .id(1L).username("trader1").password("encoded-hash")
                .email("trader1@test.com").role(Role.USER).build();

        when(userRepository.findByUsername("trader1")).thenReturn(Optional.of(existingUser));
        when(userService.generateToken(anyMap(), eq(existingUser))).thenReturn("fake-jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertEquals("fake-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("trader1", "correct-password"));
    }

    @Test
    void authenticate_invalidCredentials_propagatesException() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("trader1")
                .password("wrong-password")
                .build();

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticate(request));

        verify(userRepository, never()).findByUsername(anyString());
        verify(userService, never()).generateToken(anyMap(), any());
    }

    @Test
    void authenticate_passesCorrectClaimsToTokenGeneration() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .username("trader1")
                .password("correct-password")
                .build();

        User existingUser = User.builder()
                .id(1L).username("trader1").password("encoded-hash")
                .email("trader1@test.com").role(Role.USER).build();

        when(userRepository.findByUsername("trader1")).thenReturn(Optional.of(existingUser));
        when(userService.generateToken(anyMap(), eq(existingUser))).thenReturn("fake-jwt-token");

        authenticationService.authenticate(request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(userService).generateToken(claimsCaptor.capture(), eq(existingUser));

        assertEquals("trader1@test.com", claimsCaptor.getValue().get("email"));
        assertEquals("USER", claimsCaptor.getValue().get("role"));
    }
}