package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.mapper.PermissionMapper;
import com.wearsky.demo.user.service.impl.PermissionServiceImpl;
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
class PermissionServiceImplTest {

    @InjectMocks
    private PermissionServiceImpl permissionService;

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(permissionService, "baseMapper", permissionMapper);

        testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setName("用户读取");
        testPermission.setCode("user:read");
        testPermission.setDescription("读取用户信息");
    }

    @Test
    void updateById_ShouldClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of(10L, 20L));
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(1);

        boolean result = permissionService.updateById(testPermission);

        assertTrue(result);
        verify(stringRedisTemplate).delete(List.of("auth:authorities:10", "auth:authorities:20"));
    }

    @Test
    void updateById_NoUsers_ShouldNotClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of());
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(1);

        boolean result = permissionService.updateById(testPermission);

        assertTrue(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void updateById_Failed_ShouldNotClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of(10L));
        when(permissionMapper.updateById(any(Permission.class))).thenReturn(0);

        boolean result = permissionService.updateById(testPermission);

        assertFalse(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void removeById_ShouldClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of(10L, 20L, 30L));
        when(permissionMapper.deleteById(1L)).thenReturn(1);

        boolean result = permissionService.removeById(1L);

        assertTrue(result);
        verify(stringRedisTemplate).delete(List.of(
                "auth:authorities:10", "auth:authorities:20", "auth:authorities:30"));
    }

    @Test
    void removeById_NoUsers_ShouldNotClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of());
        when(permissionMapper.deleteById(1L)).thenReturn(1);

        boolean result = permissionService.removeById(1L);

        assertTrue(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }

    @Test
    void removeById_Failed_ShouldNotClearCache() {
        when(permissionMapper.selectUserIdsByPermissionId(1L)).thenReturn(List.of(10L));
        when(permissionMapper.deleteById(1L)).thenReturn(0);

        boolean result = permissionService.removeById(1L);

        assertFalse(result);
        verify(stringRedisTemplate, never()).delete(anyList());
    }
}
