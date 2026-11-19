package com.example.webframework.chat.controller;

import com.example.webframework.chat.dto.ChatRequest;
import com.example.webframework.chat.dto.ChatResponse;
import com.example.webframework.user.UserAccount;
import com.example.webframework.user.UserAccountRepository;
import com.example.webframework.user.UserAccountService;
import com.example.webframework.user.dto.AvatarResponse;
import com.example.webframework.user.dto.MeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final UserAccountService userAccountService;

    @MessageMapping("/chat.send") // 클라이언트가 chat.send로 보낸 메세지 처리
    @SendTo("/topic/chat") // /topic/chat을 구독한 모든 클라이언트에게 전달
    public ChatResponse send(@Valid ChatRequest request, Principal principal) {
        JwtAuthenticationToken authentication = (JwtAuthenticationToken) principal;
        Jwt jwt = authentication.getToken();

        Long accountId = Long.valueOf(jwt.getSubject());

        MeResponse me = userAccountService.me(accountId);

        return new ChatResponse(
                accountId,
                jwt.getClaimAsString("nickname"),
                request.content(), Instant.now(), me.avatar()
        );
    }

}
