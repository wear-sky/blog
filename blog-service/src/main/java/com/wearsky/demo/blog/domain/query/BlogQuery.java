package com.wearsky.demo.blog.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BlogQuery extends PageQuery {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "作者ID")
    private Long authorId;
}
