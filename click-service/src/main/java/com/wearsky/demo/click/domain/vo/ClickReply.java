package com.wearsky.demo.click.domain.vo;

import com.wearsky.demo.click.enums.Like;
import lombok.Data;

@Data
public class ClickReply {

    private Long id;

    private Long userId;

    private Long replyId;

    private Like isLike;

}
