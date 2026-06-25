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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    private static final String AUTH_AUTHORITIES_KEY_PREFIX = "auth:authorities:";

    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public boolean updateById(Permission entity) {
        List<Long> userIds = baseMapper.selectUserIdsByPermissionId(entity.getId());
        boolean result = super.updateById(entity);
        if (result) {
            clearAuthoritiesCache(userIds);
        }
        return result;
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        List<Long> userIds = baseMapper.selectUserIdsByPermissionId((Long) id);
        boolean result = super.removeById(id);
        if (result) {
            clearAuthoritiesCache(userIds);
        }
        return result;
    }

    private void clearAuthoritiesCache(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        List<String> keys = userIds.stream()
                .map(userId -> AUTH_AUTHORITIES_KEY_PREFIX + userId)
                .toList();
        try {
            stringRedisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("清除authorities缓存失败: {}", e.getMessage());
        }
    }
}
