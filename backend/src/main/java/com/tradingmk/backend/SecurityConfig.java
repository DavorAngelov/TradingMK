package com.tradingmk.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/stocks/update",  // morame allow za da postiras, smeni go~!
                                "/api/stocks/**",
                                "/ws/**",
                                "/topic/**",
                                "api/history/upload",
                                "/api/history/{symbol}"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
