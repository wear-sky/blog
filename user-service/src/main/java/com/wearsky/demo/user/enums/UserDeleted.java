package com.wearsky.demo.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserDeleted {

    NORMAL(0, "正常"),
    DELETED(1, "删除");

    @EnumValue
    private final Integer code;

    private final String message;

    UserDeleted(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
