package com.example.webframework.user;

import com.example.webframework.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @SQLRestriction("deleted = false")
@Getter
@Setter
public class UserAccount extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String nickname;

    public UserAccount(String email, String passwordHash, String nickname) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
    }

}
