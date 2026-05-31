package com.wearsky.demo.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReplyDTO {

    @Schema(description = "博客ID")
    @NotNull(message = "博客ID不能为空")
    private Long blogId;

    @Schema(description = "父回复ID（为空则为顶级回复）")
    private Long parentId;

    @Schema(description = "被回复用户ID")
    private Long replyToUserId;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;
}
