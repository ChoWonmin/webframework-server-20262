package com.example.webframework.chat.dto;

import com.example.webframework.user.dto.AvatarResponse;

import java.time.Instant;

public record ChatResponse(
        Long userAccountId,
        String nickname,
        String content,
        Instant sentAt,
        AvatarResponse avatar
) {
}
