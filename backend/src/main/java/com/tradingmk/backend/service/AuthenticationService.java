package com.tradingmk.backend.service;


import com.tradingmk.backend.dto.AuthenticationResponse;
import com.tradingmk.backend.dto.AuthenticationRequest;
import com.tradingmk.backend.dto.RegisterRequest;
import com.tradingmk.backend.model.Portfolio;
import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final PortfolioRepository portfolioRepository;


    public AuthenticationResponse register(RegisterRequest request) {
        //create user
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        //save
        repository.save(user);

        //create uniwque portfolio for user

        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user);
        portfolio.setBalance(BigDecimal.ZERO);
        portfolioRepository.save(portfolio);

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", user.getEmail());
        //final generate token
        var jwtToken = userService.generateToken(extraClaims,user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        System.out.println("Authenticating: " + request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        //it means it is authenticated
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", user.getEmail());
        //final generate token
        var jwtToken = userService.generateToken(extraClaims,user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
