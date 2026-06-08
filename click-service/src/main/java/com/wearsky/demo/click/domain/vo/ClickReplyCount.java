package com.wearsky.demo.click.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClickReplyCount {

    @Schema(description = "回复ID")
    private Long replyId;

    @Schema(description = "点赞/踩次数")
    private Long count;

}
