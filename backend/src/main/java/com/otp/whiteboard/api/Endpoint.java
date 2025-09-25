package com.otp.whiteboard.api;

public class Endpoint {
    public static final String API = "/api";

    private static final String USER = "/user";
    private static final String AUTH = "/auth";
    private static final String DRAW = "/draw";
    private static final String CURSOR = "/cursor";
    private static final String BOARD = "/board";

    public static final String USER_INTERNAL_API = API + USER;
    public static final String AUTH_INTERNAL_API = API + AUTH;
    public static final String BOARD_INTERNAL_API = API + BOARD;
    public static final String DRAW_INTERNAL_API = API + DRAW;
    public static final String CURSOR_INTERNAL_API = API + CURSOR;

    public static final String DRAW_WEBSOCKET = DRAW;
    public static final String CURSOR_WEBSOCKET = CURSOR;
}
