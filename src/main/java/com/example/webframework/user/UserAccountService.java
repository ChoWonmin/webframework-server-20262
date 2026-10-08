package com.example.webframework.user;

import com.example.webframework.user.dto.LoginRequest;
import com.example.webframework.user.dto.LoginResponse;
import com.example.webframework.user.dto.SignUpRequest;
import com.example.webframework.user.dto.SignUpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional // 서비스 전체에 걸어도 괜찮고, 함수마다 걸어도 괜찮고.
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginResponse login(LoginRequest request) {

        // BCrypt 입력 제한 : 문자 수와 UTF-8 뱌이트 수는 다르다.
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
          throw loginFailed();
        }

        UserAccount userAccount = userAccountRepository
                .findByEmailAndDeleted(request.email(), false)
                .orElseThrow(() -> loginFailed());

        // 입력한 비밀번호와 DB에 저장된 해시를 비교
        boolean matched = passwordEncoder.matches(
                request.password(), userAccount.getPasswordHash()
        );

        if (!matched) {
            throw loginFailed();
        }

        // jwt 발행 (유효시간 15분)
        Instant now = Instant.now();
        long expiresIn = 15 * 60;

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("webframework-server")
                .subject(userAccount.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("nickname", userAccount.getNickname())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new LoginResponse(accessToken, "Bearer", expiresIn);
    }

    private ResponseStatusException loginFailed() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }


    public SignUpResponse singUp(SignUpRequest request) {
        // 이메일, 닉네임 이미 디비에 있는지 체크
        // 체크했는데 이미 있으면 에러(Exception)

        if (userAccountRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."
            );
        }

        if (userAccountRepository.existsByNickname(request.nickname())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."
            );
        }

        String passwordHash = passwordEncoder.encode((request.password()));

        UserAccount userAccount = new UserAccount(request.email(), passwordHash, request.nickname());
        UserAccount saved = userAccountRepository.save(userAccount);

        return new SignUpResponse(saved.getId());
    }

    // create
    public String addUserAccount(String email, String password, String nickname) {

        UserAccount userAccount = new UserAccount(email, password, nickname);
        userAccountRepository.save(userAccount);

        return "계정 생성 success!";
    }

    public boolean checkEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    public String getUserAccountByEmail(String email) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findByEmailAndDeleted(email, false);

        if (userAccountOptional.isPresent()) {
            return userAccountOptional.get().getNickname();
        }

        return "존재하지 않는 이메일입니다.";
    }

    public String updateUserAccount(String email, String password, String nickname) {
//        Optional<UserAccount> userAccountOptional = userAccountRepository.findByEmailAndDeleted(email, false);
        UserAccount userAccount = userAccountRepository
                .findByEmailAndDeleted(email, false)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        userAccount.setNickname(nickname);
        userAccount.setPasswordHash(password);

        return "수정 success";
    }

    public String deleteUserAccount(String email) {
        // Optional<UserAccount> userAccountOptional = userAccountRepository.findByEmailAndDeleted(email, false);
        UserAccount userAccount = userAccountRepository.findByEmailAndDeleted(email, false).orElse(null);

        if (userAccount != null) {
//            userAccountRepository.delete(userAccount); // hard delete -> 에러가 나야함
            userAccount.softDelete(); // soft delete
        } else {
            return "존재하지 않는 이메일입니다.";
        }

        return "삭제 success";
    }

}
