package com.wearsky.demo.user.controller;

import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import com.wearsky.demo.user.service.IPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "权限模块")
@RestController
@RequestMapping("/user-service/permission")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final IPermissionService permissionService;

    @Operation(summary = "查询权限列表")
    @GetMapping("/query")
    public ApiResponse<PermissionPageVO> queryPermissions(PermissionQuery permissionQuery) {
        return ApiResponse.success(permissionService.queryPermission(permissionQuery));
    }

    @Operation(summary = "根据ID查询权限")
    @GetMapping("/{id}")
    public ApiResponse<Permission> getPermission(@PathVariable Long id) {
        return ApiResponse.success(permissionService.getById(id));
    }

    @Operation(summary = "创建权限")
    @PostMapping
    public ApiResponse<Void> createPermission(@RequestBody @Valid Permission permission) {
        return permissionService.save(permission) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "创建失败");
    }

    @Operation(summary = "更新权限")
    @PutMapping
    public ApiResponse<Void> updatePermission(@RequestBody @Valid Permission permission) {
        return permissionService.updateById(permission) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失败");
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        return permissionService.removeById(id) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }
}
