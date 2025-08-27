package com.otp.whiteboard.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, 
                                  @Nonnull HttpServletResponse response, 
                                  @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        
        logger.debug("Processing authentication filter for request URI: {}", request.getRequestURI());

        try {
            Optional<String> token = extractTokenFromHeader(request);
            
            if (token.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtUtil.extractUsername(token.get());
                
                if (email != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    
                    if (jwtUtil.validateToken(token.get(), email)) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        logger.info("Successfully authenticated user: {}", email);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("JWT authentication failed: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            logger.debug("Extracted Bearer token from header successfully.");
            return Optional.of(token);
        }
        return Optional.empty();
    }
}