package com.wearsky.demo.user.domain.vo;

import com.wearsky.demo.user.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RolePageVO {

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "角色列表")
    private List<Role> roles;
}