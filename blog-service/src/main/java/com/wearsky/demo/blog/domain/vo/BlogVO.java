package com.wearsky.demo.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wearsky.demo.common.domain.vo.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogVO {

    @Schema(description = "博客ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "作者信息")
    private UserVO author;
}
