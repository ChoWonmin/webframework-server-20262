package com.example.webframework.common.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.disable())
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/user-account/login", "/user-account/signup" // 로그인, 회원가입은 jwt인증 없이 호출할 수 있어야한다.
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/user-account/check-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user-account/me").authenticated()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(resource
                        -> resource.jwt((Customizer.withDefaults())));

        return http.build();
    }

}
