package com.example.webframework.chat.entity;

import com.example.webframework.common.entity.BaseEntity;
import com.example.webframework.user.UserAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "chat_message",
        indexes = @Index(
                name = "idx_chat_message_room_id",
                columnList = "chat_room_id, id"
        ) // 인덱스는 특정 방의 메세지를 ID 순서로 조회 성능 향상
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @Column(nullable = false, length = 500)
    private String content;

    public ChatMessage(ChatRoom chatRoom, UserAccount userAccount, String content) {
        this.chatRoom = chatRoom;
        this.userAccount = userAccount;
        this.content = content;
    }
}



