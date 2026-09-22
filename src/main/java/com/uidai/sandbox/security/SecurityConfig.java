package com.uidai.sandbox.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DemoBearerFilter bearerFilter) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/style.css", "/app.js",
                                "/mock/partner/callback", "/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .headers(headers ->
                        headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(bearerFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
