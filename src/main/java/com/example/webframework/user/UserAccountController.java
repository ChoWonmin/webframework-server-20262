package com.example.webframework.user;

import com.example.webframework.auth.RefreshTokenService;
import com.example.webframework.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-account")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService userAccountService;

    @GetMapping("/avatars")
    public List<AvatarResponse> getAvatars() {
        return userAccountService.getAvatars();
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<Void> updateAvatar(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateAvatarRequest request
     ) {
        Long accountId = Long.valueOf(jwt.getSubject());

        userAccountService.updateAvatar(accountId, request.avatarId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal Jwt jwt) {
        Long accountId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok()
                .body(userAccountService.me(accountId));
    }

    // login api
    // 이메일, 패스워드 -> jwt
    // jwt는 payload에 특정 정보들을 적을 수 있다. 최소한의 정보만 적는게 원칙. (보안 이슈)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = userAccountService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", response.refreshToken())
                .httpOnly(true)
                .secure(false) // 실습에서는 https가 아니라서 false
                .sameSite("Lax")
                .path("/user-account")
                .maxAge(RefreshTokenService.EXPIRES_IN_SECONDS)
                .build();

        return ResponseEntity.ok()
                .header("Cache-Control", "no-store")
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        LoginResponse response = userAccountService.refresh(refreshToken);

        return ResponseEntity.ok()
                .body(response);
    }

    // me api
    // 로그인한 회원 정보 조회
    // 개인정보 전화번호, 생년월일, 이름

    @PostMapping("/signup")
    public SignUpResponse signUp(@Valid @RequestBody SignUpRequest request) {
        return userAccountService.singUp(request);
    }

    @GetMapping("/check-email")
    public boolean checkEmail(@RequestParam String email) {
        return userAccountService.checkEmail(email);
    }


    // localhost:8080/user-account/add?email=321@naver.com&password=1234&nickname=nick321
    @GetMapping("/add")
    public String addUserAccount(String email, String password, String nickname) {
        return userAccountService.addUserAccount(email, password, nickname);
    }

    // localhost:8080/user-account/find-by-email?email=321@naver.com
    @GetMapping("/find-by-email")
    public String findUserAccountByEmail(String email) {
        return userAccountService.getUserAccountByEmail(email);
    }

    // localhost:8080/user-account/update?email=321@naver.com&password=4321&nickname=nick4321
    @GetMapping("/update")
    public String updateUserAccount(String email, String password, String nickname) {
        return userAccountService.updateUserAccount(email, password, nickname);
    }

    // localhost:8080/user-account/delete?email=321@naver.com
    @GetMapping("/delete")
    public String deleteUserAccount(String email) {
        return userAccountService.deleteUserAccount(email);
    }

}
