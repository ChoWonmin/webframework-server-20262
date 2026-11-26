package com.example.webframework.chat.service;

import com.example.webframework.chat.dto.ChatRoomCreateRequest;
import com.example.webframework.chat.dto.ChatRoomCreateResponse;
import com.example.webframework.chat.dto.ChatRoomJoinRequest;
import com.example.webframework.chat.dto.ChatRoomResponse;
import com.example.webframework.chat.entity.ChatRoom;
import com.example.webframework.chat.entity.ChatRoomMember;
import com.example.webframework.chat.repository.ChatMessageRepository;
import com.example.webframework.chat.repository.ChatRoomLikeRepository;
import com.example.webframework.chat.repository.ChatRoomMemberRepository;
import com.example.webframework.chat.repository.ChatRoomRepository;
import com.example.webframework.user.UserAccount;
import com.example.webframework.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomLikeRepository chatRoomLikeRepository;
    private final UserAccountRepository userAccountRepository;

    private final PasswordEncoder passwordEncoder;


    public void join(Long roomId, Long accountId, ChatRoomJoinRequest request) {
        // 1. 회원 확인 -> Exception
        UserAccount userAccount = userAccountRepository.findByIdAndDeleted(accountId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 계정입니다."));

        // 2. 채팅방 확인 -> Exception
        ChatRoom chatRoom = chatRoomRepository.findByIdAndDeletedFalse(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."));

        // 3. 가입 여부
        ChatRoomMember member = chatRoomMemberRepository.findByChatRoom_IdAndUserAccount_Id(roomId, accountId)
                .orElse(null);

        // 3-1. 이미 가입되어있으면 작업 종료 -> return
        if (member != null && !member.getDeleted()) {
            return;
        }

        // 가입이 안된 경우
        // 4. 비밀번호 검증 (비밀번호가 있는 방이면) -> 비밀번호 틀리면 Exception
        if (chatRoom.getPasswordHash() != null) {
            if (
                    request.password() == null
                            || request.password().getBytes(StandardCharsets.UTF_8).length > 72
                            || !passwordEncoder.matches(request.password(), chatRoom.getPasswordHash())

            ) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 올바르지 않습니다.");
            }
        }

        // 5. 가입 (신규 가입, 탈퇴 -> 복구)
        if (member == null) { // 신규
            chatRoomMemberRepository.save(
                    new ChatRoomMember(chatRoom, userAccount)
            );
        } else { // 가입했다가 탈퇴함(deleted=true)
            member.restore();
        }

    }

    public ChatRoomCreateResponse create(Long accountId, ChatRoomCreateRequest request) {
        // 1. 로그인한 회원 확인
        UserAccount owner = userAccountRepository.findByIdAndDeleted(accountId, false)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용할 수 없는 계정입니다."));

        // 2. 비밀번호가 있으면 해시 생성
        String passwordHash = null;
        String password = request.password();

        if (password != null && !password.isBlank()) {
            // BCrypt의 입력제한
            if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 UTF-8 기준 72바이트 이하여야 합니다.");
            }

            passwordHash = passwordEncoder.encode(password);
        }

        // 3. 채팅방 저장
        ChatRoom chatRoom = chatRoomRepository.save(
                new ChatRoom(
                        request.name(), request.description(), owner, passwordHash
                )
        );

        // 4. 방장도 가입 명부에 등록
        chatRoomMemberRepository.save(
                new ChatRoomMember(chatRoom, owner)
        );

        return new ChatRoomCreateResponse(chatRoom.getId());
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getRooms() {
        return chatRoomRepository.findByDeletedFalseOrderByIdDesc()
                .stream()
                .map(room -> new ChatRoomResponse(
                        room.getId(), room.getName(), room.getDescription(),
                        chatRoomMemberRepository.countByChatRoom_IdAndDeletedFalse(room.getId()),
                        room.getLikeCount(),
                        room.getPasswordHash() != null,
                        room.getCreated()
                ))
                .toList();

    }

}
