package com.example.webframework.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    public static final long EXPIRES_IN_SECONDS = 60 * 60 * 24 * 7L;

    private final SecureRandom secureRandom = new SecureRandom();
    private final RefreshTokenRepository refreshTokenRepository;

    // refresh token 발급 (생성)
    public String issue(Long userAccountId) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding().encodeToString(bytes);

        RefreshToken refreshToken = new RefreshToken(
                userAccountId,
                hash(rawToken),
                Instant.now().plusSeconds(EXPIRES_IN_SECONDS)
        );

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    // 유효한 Refresh Token 검사 + 회원 ID 반환

    @Transactional(readOnly = true)
    public Long getUserAccountId(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw invalidToken();
        }

        // RefreshToken repository로 조회하기
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHashAndDeleted(hash(rawToken), false)
                .orElseThrow(() -> invalidToken());

        // 만료시간 체크하기
        if (!refreshToken.getExpiresAt().isAfter(Instant.now())) {
            throw invalidToken();
        }

        return refreshToken.getUserAccountId();
    }

    // Refresh Token 폐기
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        refreshTokenRepository
                .findByTokenHashAndDeleted(hash(rawToken), false)
                .ifPresent(refreshToken -> refreshToken.softDelete());
    }

    private ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "다시 로그인하세요.");
    }

    private String hash(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("SHA-256을 사용할 수 없습니다.", e);
        }
    }

}
