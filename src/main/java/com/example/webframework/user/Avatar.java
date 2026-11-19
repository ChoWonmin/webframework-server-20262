package com.example.webframework.user;

import com.example.webframework.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avatar extends BaseEntity {

    private String src; // /avatar/avatar02.png
    private String bgColor;

    public Avatar(String src, String bgColor) {
        this.src = src;
        this.bgColor = bgColor;
    }
}
