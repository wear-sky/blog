package com.wearsky.demo.user.domain.vo;

import com.wearsky.demo.user.domain.entity.Permission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PermissionPageVO {

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "权限列表")
    private List<Permission> permissions;
}