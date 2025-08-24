package com.otp.whiteboard.api;

public class Endpoint {
    private static final String VERSION = "/v1";
    private static final String INTERNAL = "/internal";
    private static final String API = "/api" + VERSION;

    private static final String USER = "/user";

    private static final String INTERNAL_API = API + INTERNAL;

    public static final String USER_INTERNAL_API = INTERNAL_API + USER;
    public static final String INTERNAL_API_PATTERN = INTERNAL_API + "/**";
}
