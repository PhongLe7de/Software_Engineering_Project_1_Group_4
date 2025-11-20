package com.otp.whiteboard.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(final JwtUtil jwtUtil, final UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nonnull final HttpServletRequest request, @Nonnull final  HttpServletResponse response, @Nonnull final FilterChain filterChain) throws ServletException, IOException {
        LOGGER.debug("Processing authentication filter for request URI: {}", request.getRequestURI());

        if (request.getRequestURI().startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final Optional<String> token = extractTokenFromHeader(request);
            if (token.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                final String email = jwtUtil.extractUsername(token.get());
                if (email != null) {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtUtil.validateToken(token.get(), email)) {
                        final UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        LOGGER.info("Successfully authenticated user: {}", email);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.warn("JWT authentication failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromHeader(final HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7).trim();
            LOGGER.debug("Extracted Bearer token from header successfully.");
            return Optional.of(token);
        }
        return Optional.empty();
    }

}
