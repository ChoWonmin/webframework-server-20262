package com.example.webframework.chat.entity;

import com.example.webframework.common.entity.BaseEntity;
import com.example.webframework.user.UserAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id" , nullable = false)
    private UserAccount owner;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(name="password_hash", length = 100)
    private String passwordHash;

    public ChatRoom(String name, String description, UserAccount owner, String passwordHash) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.passwordHash = passwordHash;
    }
}
