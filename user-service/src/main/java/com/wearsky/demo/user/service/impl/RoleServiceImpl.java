package com.wearsky.demo.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import com.wearsky.demo.user.mapper.RoleMapper;
import com.wearsky.demo.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public RolePageVO queryRole(RoleQuery roleQuery) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(roleQuery.getName()), Role::getName, roleQuery.getName());
        wrapper.like(StrUtil.isNotBlank(roleQuery.getCode()), Role::getCode, roleQuery.getCode());
        wrapper.orderByDesc(Role::getCreatedAt);

        Page<Role> page = this.page(new Page<>(roleQuery.getPageNum(), roleQuery.getPageSize()), wrapper);

        RolePageVO vo = new RolePageVO();
        vo.setTotal(page.getTotal());
        vo.setRoles(page.getRecords());
        return vo;
    }
}
