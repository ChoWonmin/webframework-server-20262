package com.example.webframework.chat.dto;

import jakarta.validation.constraints.Size;

public record ChatRoomJoinRequest(
        @Size(max = 64)
        String password
) {
}
