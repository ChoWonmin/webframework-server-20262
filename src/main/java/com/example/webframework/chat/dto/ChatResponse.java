package com.example.webframework.chat.dto;

import java.time.Instant;

public record ChatResponse(
        Long userAccountId,
        String nickname,
        String content,
        Instant sentAt
) {
}
