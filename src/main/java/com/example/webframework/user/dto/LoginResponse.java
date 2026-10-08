package com.example.webframework.user.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn // 토큰 유효 기간 (초단위)
) {
}
