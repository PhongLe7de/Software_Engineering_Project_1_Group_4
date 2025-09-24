package com.otp.whiteboard.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otp.whiteboard.model.Stroke;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class DrawSubscriber implements MessageListener {

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

            // Push to WebSocket clients subscribed to /topic/drawing-session-{sessionId}
            messagingTemplate.convertAndSend("/topic/drawing-session-" + event.getId(), event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}