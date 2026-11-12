package com.example.webframework.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn, // 토큰 유효 기간 (초단위)
        @JsonIgnore String refreshToken
        // HttpOnly의 일기 제한은 쿠키에만 적용
) {
}
