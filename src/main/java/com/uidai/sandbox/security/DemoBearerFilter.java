package com.uidai.sandbox.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Demo bearer-token middleware for the assessment.
 *
 * Production note:
 * Replace the demo token lookup with OAuth2 Resource Server JWT validation
 * against the organization's issuer/JWKS endpoint.
 *
 * Demo tokens:
 *   demo-write-token -> partner_A + write:risk_test + read:risk_test
 *   demo-read-token  -> partner_A + read:risk_test
 *   partner-b-token  -> partner_B + read:risk_test
 */
@Component
public class DemoBearerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            DemoPrincipal principal = switch (token) {
                case "demo-write-token" ->
                        new DemoPrincipal("partner_A",
                                List.of("SCOPE_write:risk_test", "SCOPE_read:risk_test"));
                case "demo-read-token" ->
                        new DemoPrincipal("partner_A",
                                List.of("SCOPE_read:risk_test"));
                case "partner-b-token" ->
                        new DemoPrincipal("partner_B",
                                List.of("SCOPE_read:risk_test"));
                default -> null;
            };

            if (principal != null) {
                var authorities = principal.scopes().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                var authentication = new UsernamePasswordAuthenticationToken(
                        principal.partnerId(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    public record DemoPrincipal(String partnerId, List<String> scopes) {}
}
