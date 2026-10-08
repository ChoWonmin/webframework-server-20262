package com.example.webframework.common.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.HexFormat;

@Configuration
public class JwtConfig {

    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${JWT_SECRET}") String secret
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);

//        System.out.println(Base64.getUrlEncoder()
//                .withoutPadding()
//                .encodeToString(keyBytes));

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET은 디코딩 후 32바이트 이상이어야 합니다.");
        }

        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }
}
