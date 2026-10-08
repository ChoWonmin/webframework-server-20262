package com.example.webframework.common.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant lastUpdate;

    @Column(nullable = false)
    private Boolean deleted = false; // soft delete 용도

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        created = now;
        lastUpdate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdate = Instant.now();
    }

    @PreRemove
    protected void preventHardDelete() {
        throw new IllegalStateException("Use softDelete instead of remove/delete."); // hard delete 방지
    }

    public void softDelete() {
        deleted = true;
    }
}
