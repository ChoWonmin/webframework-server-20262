package com.example.webframework.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank(message = "메세지를 입력하세요.")
        @Size(max = 500, message = "메세지는 500자 이하여야합니다.")
        String content
) {
}
