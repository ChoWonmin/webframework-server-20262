package com.example.webframework.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    List<Avatar> findByDeletedFalse();
    List<Avatar> findByDeletedFalseOrderByIdDesc();

    Optional<Avatar> findByIdAndDeletedFalse(Long avatarId);
}
