package com.wearsky.demo.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Roles {

    ADMIN(1L, "超级管理员"),
    USER(2L, "普通用户");

    @EnumValue
    private final Long code;

    private final String message;

    Roles(Long code, String message) {
        this.code = code;
        this.message = message;
    }
}
