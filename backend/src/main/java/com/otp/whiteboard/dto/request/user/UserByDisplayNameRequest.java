package com.otp.whiteboard.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserByDisplayNameRequest(
        @JsonProperty("display_name")  String displayName
) {}
