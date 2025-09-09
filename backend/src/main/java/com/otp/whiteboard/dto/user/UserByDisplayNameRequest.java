package com.otp.whiteboard.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserByDisplayNameRequest(
        @JsonProperty("display_name")  String displayName
) {}
