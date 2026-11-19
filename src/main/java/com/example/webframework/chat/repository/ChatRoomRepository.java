package com.example.webframework.chat.repository;

import com.example.webframework.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 삭제되지 않은 채팅방 목록
    List<ChatRoom> findByDeletedFalseOrderByIdDesc();

    // 삭제되지 않은 채팅방 ID로 조회
    Optional<ChatRoom> findByIdAndDeletedFalse(Long id);

    @Transactional
    @Modifying( // 변경 쿼리임을 지정. (조회 쿼리가 아님)
            flushAutomatically = true, // 실행 전에 대기 중인 엔티티 변경을 DB에 반영
            clearAutomatically = true // 실행 후 영속성 컨텍스트를 비워, 다음 조회에서 변경된 DB 값을 읽는다.
    )
    @Query("""
        update ChatRoom room
        set room.likeCount = room.likeCount + 1, room.lastUpdate = :now
        where room.id = :roomId and room.deleted = false
    """) // jpql
    int increaseLikeCount(
        @Param("roomId") Long roomId,
        @Param("now") Instant now
    );
}
