package com.wearsky.demo.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import com.wearsky.demo.user.mapper.PermissionMapper;
import com.wearsky.demo.user.service.IPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Override
    public PermissionPageVO queryPermission(PermissionQuery permissionQuery) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(permissionQuery.getName()), Permission::getName, permissionQuery.getName());
        wrapper.like(StrUtil.isNotBlank(permissionQuery.getCode()), Permission::getCode, permissionQuery.getCode());
        wrapper.orderByDesc(Permission::getCreatedAt);

        Page<Permission> page = this.page(new Page<>(permissionQuery.getPageNum(), permissionQuery.getPageSize()), wrapper);

        PermissionPageVO vo = new PermissionPageVO();
        vo.setTotal(page.getTotal());
        vo.setPermissions(page.getRecords());
        return vo;
    }
}
