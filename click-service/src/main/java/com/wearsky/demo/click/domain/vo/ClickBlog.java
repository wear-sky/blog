package com.wearsky.demo.click.domain.vo;

import com.wearsky.demo.click.enums.Like;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClickBlog {

    @Schema(description = "点赞/踩ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "博客ID")
    private Long blogId;

    @Schema(description = "点赞/踩 1、点赞 0、点踩")
    private Like isLike;

}
