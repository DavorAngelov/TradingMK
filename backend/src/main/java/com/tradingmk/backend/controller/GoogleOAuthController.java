package com.tradingmk.backend.controller;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tradingmk.backend.dto.GoogleAuthRequest;
import com.tradingmk.backend.model.AuthProvider;
import com.tradingmk.backend.model.Portfolio;
import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.repository.PortfolioRepository;
import com.tradingmk.backend.repository.UserRepository;
import com.tradingmk.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth/google")
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PortfolioRepository portfolioRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    private final String GOOGLE_CLIENT_ID = "72179147890-t9evep4qgldanqeosjp8bm30flq8bnkr.apps.googleusercontent.com";
    private final String GOOGLE_CLIENT_SECRET = "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";
    private final String GOOGLE_REDIRECT_URI = "http://localhost:5173";
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USER_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @PostMapping
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String tokenString = request.get("credential"); // id token od frontend

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

            GoogleIdToken idToken = verifier.verify(tokenString);

            if (idToken == null) {
                return ResponseEntity.status(401).body("Invalid ID token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");


            User user = userRepository.findByEmail(email).orElse(null);


            // case A: email not found
            if (user == null) {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(email);
                newUser.setFullName(name);
                newUser.setRole(Role.USER);
                newUser.setBalance(0.0);
                newUser.setAuthProviders(Set.of(AuthProvider.GOOGLE));
                userRepository.save(newUser);

                Portfolio portfolio = new Portfolio();
                portfolio.setUser(newUser);
                portfolio.setBalance(BigDecimal.ZERO);
                portfolioRepository.save(portfolio);

                String jwt = userService.generateToken(newUser);
                return ResponseEntity.ok(Collections.singletonMap("token", jwt));
            }

            // case B: email exists but ony intternal
            if (user.getAuthProviders().contains(AuthProvider.INTERNAL) &&
                    !user.getAuthProviders().contains(AuthProvider.GOOGLE)) {
                return ResponseEntity.status(403).body(
                        "Веќе имате акаунт со е-пошта и лозинка. Најавете се прво со вашата лозинка. " +
                                "Потоа во профилот може да го поврзете Google."
                );
            }

            // case c: already has google
            if (user.getAuthProviders().contains(AuthProvider.GOOGLE)) {
                String jwt = userService.generateToken(user);
                return ResponseEntity.ok(Collections.singletonMap("token", jwt));
            }

            // fallback
            return ResponseEntity.status(403).body("neuspesen login so google.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("server error: " + e.getMessage());
        }
    }

}
