package com.wearsky.demo.blog.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class BlogPageVO {

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "博客列表")
    private List<BlogVO> blogs;
}
