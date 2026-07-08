package com.tradingmk.backend.unit;

import com.tradingmk.backend.model.Role;
import com.tradingmk.backend.model.User;
import com.tradingmk.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService();

        user = User.builder()
                .id(1L)
                .username("trader1")
                .password("encoded-password")
                .email("trader1@test.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void generateToken_thenExtractUsername_returnsOriginalUsername() {
        String token = userService.generateToken(user);

        assertEquals("trader1", userService.extractUsername(token));
    }

    @Test
    void generateToken_withExtraClaims_claimsAreEmbedded() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", user.getEmail());
        extraClaims.put("role", user.getRole().name());

        String token = userService.generateToken(extraClaims, user);

        String email = userService.extractClaim(token, claims -> claims.get("email", String.class));
        String role = userService.extractClaim(token, claims -> claims.get("role", String.class));

        assertEquals("trader1@test.com", email);
        assertEquals("USER", role);
    }

    @Test
    void isTokenValid_matchingUser_returnsTrue() {
        String token = userService.generateToken(user);

        assertTrue(userService.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_mismatchedUsername_returnsFalse() {
        String token = userService.generateToken(user);

        User otherUser = User.builder()
                .id(2L)
                .username("someone_else")
                .password("x")
                .email("other@test.com")
                .role(Role.USER)
                .build();

        assertFalse(userService.isTokenValid(token, otherUser));
    }

    @Test
    void extractAllClaims_containsSubjectAndIssuedAt() {
        String token = userService.generateToken(user);

        var claims = userService.extractAllClaims(token);

        assertEquals("trader1", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}