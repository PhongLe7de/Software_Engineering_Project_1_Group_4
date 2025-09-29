package com.otp.whiteboard.dto.drawing;

/**
 * Simple request object for asking drawing history.
 * Sent from the client with boardId and optional limit.
 */

public record HistoryRequest(Long boardId, Integer limit) { }
