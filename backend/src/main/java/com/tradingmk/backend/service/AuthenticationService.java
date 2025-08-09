package com.tradingmk.backend.service;


import com.tradingmk.backend.dto.AuthenticationResponse;
import com.tradingmk.backend.dto.AuthenticationRequest;
import com.tradingmk.backend.dto.RegisterRequest;
import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = userService.generateToken(user);
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

        var jwtToken = userService.generateToken(user);
        System.out.println("Generated JWT: " + jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
