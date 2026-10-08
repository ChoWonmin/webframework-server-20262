package com.example.webframework.common.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;

@Configuration
public class JwtConfig {

    // 발급과 검증에서 함께 사용할 키
    @Bean
    public SecretKey jwtKey(@Value("${JWT_SECRET}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

//        System.out.println(Base64.getUrlEncoder()
//                .withoutPadding()
//                .encodeToString(keyBytes));

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 디코딩 후 32바이트 이상이어야 합니다.");
        }

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    // JWT 발급
    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    // JWT 검증
    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();

        // 만료 시간 오차 허용하지 않겠다.
        JwtTimestampValidator time = new JwtTimestampValidator(Duration.ZERO);

        JwtIssuerValidator issuer = new JwtIssuerValidator("webframework-server");

        // exp 존재, sub 양수인지 체크 (custom한 validator 만들기)
        OAuth2TokenValidator<Jwt> requiredClaims = jwt -> {
            boolean validSubjecct;

            try {
                validSubjecct = Long.parseLong(jwt.getSubject()) > 0;
            } catch (RuntimeException ex) {
                validSubjecct = false;
            }

            if (jwt.getExpiresAt() == null || !validSubjecct) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Invalid JWT token.", null)
                );
            }

            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(time, issuer, requiredClaims)
        ); // validator 적용

        return decoder;
    }
}
