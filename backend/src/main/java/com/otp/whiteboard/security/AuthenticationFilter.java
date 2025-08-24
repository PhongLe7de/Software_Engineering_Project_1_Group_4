package com.otp.whiteboard.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(org.springframework.security.web.authentication.AuthenticationFilter.class);

    public AuthenticationFilter() {
    }

    @Override
    protected void doFilterInternal(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response, @Nonnull final FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("**Processing authentication filter for request URI: {}**", request.getRequestURI());

        try{
            final Optional<String> authToken = extractTokenFromHeader(request);
            if(authToken.isPresent()){
                final FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(authToken.get());
                final FirebaseAuthenticationContext authentication = new FirebaseAuthenticationContext(decodedToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("**Successfully authenticated Firebase user UID: {}**", decodedToken.getUid());
            }
        } catch (FirebaseAuthException e) {
            logger.warn("**Authentication failed: Invalid Firebase token. Reason: {}**", e.getMessage());
            handleAuthenticationError(response, e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private Optional<String> extractTokenFromHeader(final HttpServletRequest request) {
        final String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7).trim();
            logger.debug("**Extracted Bearer token from header successfully.**");
            return Optional.of(token);
        }
        return Optional.empty();
    }

    private void handleAuthenticationError(@Nonnull final HttpServletResponse response, @Nonnull final FirebaseAuthException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String errorJson = "{\"error\": \"Access token is invalid\", \"details\": \"" + e.getMessage() + "\"}";
        response.getWriter().write(errorJson);
        response.getWriter().flush();
    }
}
