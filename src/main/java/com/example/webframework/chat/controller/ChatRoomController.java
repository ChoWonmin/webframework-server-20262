package com.example.webframework.chat.controller;

import com.example.webframework.chat.dto.ChatRoomCreateRequest;
import com.example.webframework.chat.dto.ChatRoomCreateResponse;
import com.example.webframework.chat.dto.ChatRoomJoinRequest;
import com.example.webframework.chat.dto.ChatRoomResponse;
import com.example.webframework.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomCreateResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChatRoomCreateRequest request
    ) {
        Long accountId = Long.valueOf(jwt.getSubject());

        ChatRoomCreateResponse response = chatRoomService.create(accountId, request);

        return ResponseEntity.ok(response);
    }

    // get methods
    @GetMapping
    public List<ChatRoomResponse> getRooms() {
        return chatRoomService.getRooms();
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> join(
            @AuthenticationPrincipal Jwt jwt, @PathVariable("roomId") Long roomId, @Valid @RequestBody ChatRoomJoinRequest request
            ) {
        Long accountId = Long.valueOf(jwt.getSubject());

        chatRoomService.join(roomId, accountId, request);

        return ResponseEntity.noContent().build();
    }
}
