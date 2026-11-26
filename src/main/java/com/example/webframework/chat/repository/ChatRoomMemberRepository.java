package com.example.webframework.chat.repository;

import com.example.webframework.chat.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    // 현재 가입 중인지 확인
        // 어떤 채팅방인지 + 어떤 사용자인지
    boolean existsByChatRoom_IdAndUserAccount_IdAndDeletedFalse(Long chatRoomId, Long userAccountId);

    // 탈퇴한 기록까지 조회 : 재가입 시 복구에 사용
    Optional<ChatRoomMember> findByChatRoom_IdAndUserAccount_Id(Long chatRoomId, Long userAccountId);

    // 현재 가입자 수
    long countByChatRoom_IdAndDeletedFalse(Long chatRoomId);


}
