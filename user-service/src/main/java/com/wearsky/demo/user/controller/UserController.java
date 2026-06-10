package com.wearsky.demo.user.controller;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.common.domain.vo.UserVO;
import com.wearsky.demo.user.domain.dto.LoginDTO;
import com.wearsky.demo.user.domain.dto.RegisterDTO;
import com.wearsky.demo.user.domain.dto.UpdateUserDTO;
import com.wearsky.demo.user.domain.entity.User;
import com.wearsky.demo.user.domain.query.UserQuery;
import com.wearsky.demo.user.domain.vo.UserPageVO;
import com.wearsky.demo.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户模块")
@RestController
@RequestMapping("/user-service/user")
@AllArgsConstructor
public class UserController {

    IUserService userServiceImpl;

    PasswordEncoder passwordEncoder;

    @Operation(summary = "注册")
    @PostMapping
    public ApiResponse<Object> register(@RequestBody @Valid RegisterDTO registerDTO) {
        return userServiceImpl.register(BeanUtil.toBean(registerDTO, User.class)) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "注册失败");
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        return ApiResponse.success(userServiceImpl.login(loginDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询用户", description = "根据条件查询用户")
    @GetMapping("/query")
    public ApiResponse<UserPageVO> queryUsers(UserQuery userQuery) {
        return ApiResponse.success(userServiceImpl.queryUser(userQuery));
    }

    @Operation(summary = "更新用户", description = "更新本人用户信息")
    @PutMapping
    public ApiResponse<Void> updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO) {
        User user = BeanUtil.toBean(updateUserDTO, User.class);
        user.setId((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        String rawPassword = user.getPassword();
        if (StringUtils.isNotBlank(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userServiceImpl.updateById(user) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "更新失败");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        return userServiceImpl.deleteUserById(id) ?
                ApiResponse.success() : ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "删除失败");
    }

    @Operation(summary = "获取信息", description = "获取本人用户信息")
    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        User user = userServiceImpl
                .getById((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ApiResponse.success(BeanUtil.toBean(user, UserVO.class));
    }

    @Operation(summary = "获取信息", description = "根据用户ID获取用户信息")
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUser(@PathVariable Long id) {
        User user = userServiceImpl.getById(id);
        return ApiResponse.success(BeanUtil.toBean(user, UserVO.class));
    }

    @Operation(summary = "获取用户信息列表", description = "根据用户ID列表获取用户信息列表")
    @GetMapping("/ids")
    public ApiResponse<List<UserVO>> getUserByIds(@RequestParam List<Long> ids) {
        List<User> users = userServiceImpl.getbyids(ids);
        return ApiResponse.success(BeanUtil.copyToList(users, UserVO.class));
    }
}
