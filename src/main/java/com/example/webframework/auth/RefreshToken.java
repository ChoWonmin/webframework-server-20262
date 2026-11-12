package com.example.webframework.auth;

import com.example.webframework.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Column(name = "user_account_id", nullable = false)
    private Long userAccountId;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public RefreshToken(
            Long userAccountId,
            String tokenHash,
            Instant expiresAt
    ) {
        this.userAccountId = userAccountId;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }
}