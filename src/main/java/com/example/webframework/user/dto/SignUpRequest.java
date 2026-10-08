package com.example.webframework.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record SignUpRequest(
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min=8, max=64, message = "비밀번호는 8자~64자 입니다.")
        String password,

        @NotBlank(message = "닉네임을 입력하세요.")
        @Size(min=2, max=20, message = "닉네임은 2~20자 입니다.")
        String nickname
) {
    public SignUpRequest {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        nickname = nickname == null ? null : nickname.trim();
    }

    // 비빌번호 형식 체크는 다음주
}
