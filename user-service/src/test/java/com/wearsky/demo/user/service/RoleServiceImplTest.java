package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.mapper.RoleMapper;
import com.wearsky.demo.user.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    private Role testRole;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(roleService, "baseMapper", roleMapper);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("管理员");
        testRole.setCode("ADMIN");
        testRole.setDescription("系统管理员");
    }

    @Test
    void updateById_ShouldClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of(10L, 20L));
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        boolean result = roleService.updateById(testRole);

        assertTrue(result);
        verify(stringRedisTemplate).delete(List.of("auth:authorities:10", "auth:authorities:20"));
    }

    @Test
    void updateById_NoUsers_ShouldNotClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of());
        when(roleMapper.updateById(any(Role.class))).thenReturn(1);

        boolean result = roleService.updateById(testRole);

        assertTrue(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void updateById_Failed_ShouldNotClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of(10L));
        when(roleMapper.updateById(any(Role.class))).thenReturn(0);

        boolean result = roleService.updateById(testRole);

        assertFalse(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void removeById_ShouldClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of(10L, 20L, 30L));
        when(roleMapper.deleteById(1L)).thenReturn(1);

        boolean result = roleService.removeById(1L);

        assertTrue(result);
        verify(stringRedisTemplate).delete(List.of(
                "auth:authorities:10", "auth:authorities:20", "auth:authorities:30"));
    }

    @Test
    void removeById_NoUsers_ShouldNotClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of());
        when(roleMapper.deleteById(1L)).thenReturn(1);

        boolean result = roleService.removeById(1L);

        assertTrue(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void removeById_Failed_ShouldNotClearCache() {
        when(roleMapper.selectUserIdsByRoleId(1L)).thenReturn(List.of(10L));
        when(roleMapper.deleteById(1L)).thenReturn(0);

        boolean result = roleService.removeById(1L);

        assertFalse(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }
}
