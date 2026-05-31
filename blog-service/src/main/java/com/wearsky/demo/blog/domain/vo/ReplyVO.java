package com.wearsky.demo.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyVO {

    @Schema(description = "回复ID")
    private Long id;

    @Schema(description = "博客ID")
    private Long blogId;

    @Schema(description = "父回复ID")
    private Long parentId;

    @Schema(description = "被回复用户ID")
    private Long replyToUserId;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "回复人ID")
    private Long userId;

    @Schema(description = "回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
