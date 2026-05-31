package com.wearsky.demo.user.controller;

import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import com.wearsky.demo.user.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色模块")
@RestController
@RequestMapping("/user-service/role")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final IRoleService roleService;

    @Operation(summary = "查询角色列表")
    @GetMapping("/query")
    public ApiResponse<RolePageVO> queryRoles(RoleQuery roleQuery) {
        return ApiResponse.success(roleService.queryRole(roleQuery));
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public ApiResponse<Role> getRole(@PathVariable Long id) {
        return ApiResponse.success(roleService.getById(id));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public ApiResponse<Void> createRole(@RequestBody @Valid Role role) {
        return roleService.save(role) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "创建失败");
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public ApiResponse<Void> updateRole(@RequestBody @Valid Role role) {
        return roleService.updateById(role) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失败");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        return roleService.removeById(id) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }
}
