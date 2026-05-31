package com.wearsky.demo.click.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum Like {

    LIKE(1L, "点赞"),
    DISLIKE(0L, "点踩");

    @EnumValue
    private final Long code;

    private final String message;

    Like(Long code, String message) {
        this.code = code;
        this.message = message;
    }
}
