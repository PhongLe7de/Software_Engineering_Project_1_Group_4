package com.otp.whiteboard.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otp.whiteboard.model.Stroke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class DrawSubscriber implements MessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DrawSubscriber.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public DrawSubscriber(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onMessage(final Message message,final  byte[] pattern) {
        try {
            final String msg = message.toString();
            // Deserialize JSON into DrawingEvent
            final Stroke event = objectMapper.readValue(msg, Stroke.class);
            // Send the event to WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/drawing-session-" + event.getId(), event);

        } catch (Exception e) {
            LOGGER.error("Error handling Redis message", e);
        }
    }
}