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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    private static final String AUTH_AUTHORITIES_KEY_PREFIX = "auth:authorities:";

    private final StringRedisTemplate stringRedisTemplate;

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

    @Override
    public boolean updateById(Role entity) {
        List<Long> userIds = baseMapper.selectUserIdsByRoleId(entity.getId());
        boolean result = super.updateById(entity);
        if (result) {
            clearAuthoritiesCache(userIds);
        }
        return result;
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        List<Long> userIds = baseMapper.selectUserIdsByRoleId((Long) id);
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
