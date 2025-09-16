package com.otp.whiteboard.listener;

public class DrawSubscriber {
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        // TODO: PUSH TO WEBSOCKET
    }
}
