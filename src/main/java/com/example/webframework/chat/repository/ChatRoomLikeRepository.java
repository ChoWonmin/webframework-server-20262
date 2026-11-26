package com.example.webframework.chat.repository;

import com.example.webframework.chat.entity.ChatRoomLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomLikeRepository extends JpaRepository<ChatRoomLike, Long> {

    // 기록 없음 -> 새로 좋아요 등록
    // deleted = false -> 이미 좋아요 누른 상태
    // deleted = true -> 이미 좋아요를 눌렀다가 취소한 상태

    Optional<ChatRoomLike> findByChatRoom_IdAndUserAccount_Id(Long chatRoomId, Long userAccountId);

}
