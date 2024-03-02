package com.secureauthenticationapp.authenticationapp.unitTest;

import com.secureauthenticationapp.authenticationapp.config.SecurityConfig;
import com.secureauthenticationapp.authenticationapp.domain.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class TestSecurityConfig extends SecurityConfig {
    public TestSecurityConfig(UserService userService) {
        super(userService);
    }
    @Bean
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/blog/**")
                        .permitAll()
                );
        return http.build();
    }
}

