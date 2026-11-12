package com.example.webframework.chat;

// interceptor
// 통신 중간에 통신을 가로채서 특정한 로직을 수행함

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

// ws stomp 통신에서 jwt 인식하는 로직
// jwt 검증 - Access Token
// 성공 -> 해당 연결에 사용자 정보 연결
// 실패 -> 연결 거부
@Component
@RequiredArgsConstructor
public class ChatAuthInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) { // 메세지가 처리되기 전
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        // STOMP 연경 요청일 때 JWT 검증
        if (StompCommand.CONNECT.equals(command)) { // CONNECT, SEND, SUBSCRIBE ...
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new BadCredentialsException("로그인이 필요합니다.");
            }

            String token = authorization.substring("Bearer ".length());

            // 기존 JwtConfig에 설정한 검증 실행
            Jwt jwt = jwtDecoder.decode(token);

            JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                    jwt, List.of(), jwt.getSubject()
            );

            accessor.setUser(authentication);
        }

        if (command == StompCommand.SEND || command == StompCommand.SUBSCRIBE) {
            // 1. connect에서 인증한 사용자 확인
            if ( !(accessor.getUser() instanceof JwtAuthenticationToken authentication)
                || !authentication.isAuthenticated()
            ) {
                throw new BadCredentialsException(("로그인이 필요합니다."));
            }

            // 2. 연결 이후 토큰이 만료되었는지 확인
            Instant expiresAt = authentication.getToken().getExpiresAt();

            if (expiresAt == null || Instant.now().isAfter(expiresAt)) {
                throw new BadCredentialsException("인증이 만료되었습니다.");
            }

            // 3. 명령에 맞는 목저지인지 확인
            String destination = accessor.getDestination();

            // SEND /app/chat.send controller에 채팅 전송
            // SUBSCRIBE /topic/chat 채팅 메세지 수신 구독

            if (command == StompCommand.SEND && !"/app/chat.send".equals(destination)) {
                throw new AccessDeniedException(("전송할 수 없는 주소입니다."));
            }

            if (command == StompCommand.SUBSCRIBE && !"/topic/chat".equals(destination)) {
                throw new AccessDeniedException(("전송할 수 없는 주소입니다."));
            }
        }

        return message;
    }
}
