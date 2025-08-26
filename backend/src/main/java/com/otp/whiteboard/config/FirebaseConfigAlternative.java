package com.otp.whiteboard.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Alternative Firebase configuration using environment variables
 * Use this instead of FirebaseConfig if you prefer environment-based setup
 */
// @Configuration
public class FirebaseConfigAlternative {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfigAlternative.class);

    @Value("${firebase.service-account-key:}")
    private String serviceAccountKey;

    @Value("${firebase.project-id:}")
    private String projectId;

    @PostConstruct
    public void init() throws IOException {
        logger.info("Initializing Firebase configuration from environment variables...");
        
        try {
            InputStream serviceAccount;
            
            if (!serviceAccountKey.isEmpty()) {
                // Use service account key from environment variable
                serviceAccount = new ByteArrayInputStream(serviceAccountKey.getBytes());
            } else {
                // Fallback to file-based configuration
                serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json");
                if (serviceAccount == null) {
                    logger.error("Firebase service account not configured. Please set firebase.service-account-key or add firebase-service-account.json");
                    throw new IOException("Firebase service account not configured");
                }
            }

            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount));
            
            if (!projectId.isEmpty()) {
                optionsBuilder.setProjectId(projectId);
            }

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(optionsBuilder.build());
                logger.info("Firebase initialized successfully");
            } else {
                logger.info("Firebase already initialized");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase: {}", e.getMessage());
            throw e;
        }
    }
}