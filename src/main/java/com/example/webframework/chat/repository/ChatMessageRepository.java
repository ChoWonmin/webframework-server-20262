package com.example.webframework.chat.repository;

import com.example.webframework.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 메세지 불러오기
    // 나중에 페이징 처리 size, page, offset
        // N size : N개씩 불러온다
        // n page : n번째 페이지부터 불러온다.

    // 해당 방의 최근 메세지 50개
    List<ChatMessage> findTop50ByChatRoom_IdAndDeletedFalseOrderByIdDesc(Long chatRoomId);


}
