package com.example.webframework.chat.dto;

import java.time.Instant;

public record ChatRoomResponse(
        Long id, String name, String description,
        long memberCount, long likeCount,
        boolean hasPassword,
        Instant created
) {
}
