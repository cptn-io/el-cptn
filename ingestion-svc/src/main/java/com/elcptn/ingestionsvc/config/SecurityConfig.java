package com.elcptn.ingestionsvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/* @author: kc, created on 4/13/23 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public String[] PUBLIC_PAGES = new String[]{"/event/**"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers(PUBLIC_PAGES)).authorizeHttpRequests(authz -> authz
                .requestMatchers(PUBLIC_PAGES).permitAll());

        return http.build();
    }
}
