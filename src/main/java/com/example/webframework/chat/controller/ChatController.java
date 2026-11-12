package com.example.webframework.chat.controller;

import com.example.webframework.chat.dto.ChatRequest;
import com.example.webframework.chat.dto.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatController {

    @MessageMapping("/chat.send") // 클라이언트가 chat.send로 보낸 메세지 처리
    @SendTo("/topic/chat") // /topic/chat을 구독한 모든 클라이언트에게 전달
    public ChatResponse send(@Valid ChatRequest request, Principal principal) {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) principal;
        Jwt jwt = authentication.getToken();

        return new ChatResponse(
                Long.valueOf(jwt.getSubject()),
                jwt.getClaimAsString("nickname"),
                request.content(), Instant.now()
        );
    }

}
