package com.example.webframework.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRoomCreateRequest(
        @NotBlank(message = "채팅방 이름을 입력하세요.")
        @Size(max = 100)
        String name,
        @Size(max = 200)
        String description,
        @Size(max = 64)
        String password

) {
        public ChatRoomCreateRequest {
            name = name == null ? null : name.trim();
            description = description == null ? null : description.trim();
        }
}
