package com.wearsky.demo.user.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageQuery {

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页大小")
    private Integer pageSize = 5;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "是否升序")
    private Boolean isAsc;

}
