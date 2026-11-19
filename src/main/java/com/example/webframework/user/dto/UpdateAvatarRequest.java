package com.example.webframework.user.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateAvatarRequest(
        @NotNull @Positive Long avatarId
) {
}
