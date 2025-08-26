package com.otp.whiteboard.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import jakarta.annotation.Nonnull;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirebaseAuthenticationContext extends AbstractAuthenticationToken {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseAuthenticationContext.class);
    private final FirebaseToken firebaseToken;

    public FirebaseAuthenticationContext(@Nonnull final FirebaseToken firebaseToken) {
        super(null);
        this.firebaseToken = firebaseToken;
        setAuthenticated(true);
    }

    @Override
    public FirebaseToken getCredentials() {
        return firebaseToken;
    }

    @Override
    public UserRecord getPrincipal() {
        final String uid = firebaseToken.getUid();
        try {
            return FirebaseAuth.getInstance().getUser(uid);
        } catch (Exception e) {
            logger.error("Failed to retrieve user record with UID: {}", uid, e);
            return null;
        }
    }
}
