package com.example.webframework.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByIdAndDeleted(Long id, Boolean deleted);

    List<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByEmailAndDeleted(String email, boolean deleted);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

}
