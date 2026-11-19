package com.example.webframework.chat.entity;

import com.example.webframework.common.entity.BaseEntity;
import com.example.webframework.user.UserAccount;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
     name = "chat_room_id",
     uniqueConstraints = @UniqueConstraint(
             name = "uk_chat_room_like_room_user",
             columnNames = { "chat_room_id", "user_account_id" }
     )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn( name="chat_room_id", nullable = false )
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn( name="user_account_id", nullable = false )
    private UserAccount userAccount;

    public ChatRoomLike(ChatRoom chatRoom, UserAccount userAccount) {
        this.chatRoom = chatRoom;
        this.userAccount = userAccount;
    }
}
