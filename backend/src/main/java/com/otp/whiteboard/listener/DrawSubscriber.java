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
    private static final Logger logger = LoggerFactory.getLogger(DrawSubscriber.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public DrawSubscriber(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msg = message.toString();
            // Deserialize JSON into DrawingEvent
            Stroke event = objectMapper.readValue(msg, Stroke.class);
            // Send the event to WebSocket subscribers
            messagingTemplate.convertAndSend("/topic/drawing-session-" + event.getId(), event);

        } catch (Exception e) {
            logger.error("Error handling Redis message", e);
        }
    }
}