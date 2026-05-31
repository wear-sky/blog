package com.wearsky.demo.user.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends PageQuery {

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;
}