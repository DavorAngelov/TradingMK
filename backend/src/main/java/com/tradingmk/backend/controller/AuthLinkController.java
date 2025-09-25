package com.tradingmk.backend.controller;

import com.tradingmk.backend.model.AuthProvider;
import com.tradingmk.backend.model.PendingLink;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.PendingLinkRepository;
import com.tradingmk.backend.repository.UserRepository;
import com.tradingmk.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth/link")
@RequiredArgsConstructor
public class AuthLinkController {

    private final PendingLinkRepository pendingLinkRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @PostMapping("/confirm")
    @Transactional //need transactionalll!!!!
    public ResponseEntity<?> confirmLink(@RequestBody Map<String, String> body) {
        String pendingToken = body.get("pendingToken");
        String usernameOrEmail = body.get("username");
        String password = body.get("password");

        if (pendingToken == null || usernameOrEmail == null || password == null) {
            return ResponseEntity.badRequest().body("missing fields");
        }

        PendingLink pending = pendingLinkRepository.findByToken(pendingToken).orElse(null);
        if (pending == null || pending.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(410).body("pending token invalid or expired");
        }

        // auth internal credentials
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid internal credentials");
        }

        // load user
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail).orElse(null));
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // verufy
        if (!user.getEmail().equalsIgnoreCase(pending.getEmail())) {
            return ResponseEntity.status(403).body("Pending link does not match authenticated user");
        }


        Set<AuthProvider> providers = user.getAuthProviders();
        providers.add(AuthProvider.GOOGLE);
        user.setAuthProviders(providers);
        userRepository.save(user);

        // delete pending
        pendingLinkRepository.deleteByToken(pendingToken);

        // generate token
        String jwt = userService.generateToken(user);

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", jwt);
        resp.put("message", "Google account linked and logged in");

        return ResponseEntity.ok(resp);
    }
}
