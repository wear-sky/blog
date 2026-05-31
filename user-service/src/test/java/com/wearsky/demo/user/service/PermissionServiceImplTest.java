package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PermissionServiceImplTest {

    @Autowired
    private IPermissionService permissionService;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setName("测试权限");
        testPermission.setCode("test:permission");
        testPermission.setDescription("测试权限描述");
    }

    @Test
    void queryPermission_Success() {
        // Given
        permissionService.save(testPermission);

        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.setPageNum(1);
        permissionQuery.setPageSize(10);
        permissionQuery.setName("测试");

        // When
        PermissionPageVO result = permissionService.queryPermission(permissionQuery);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
        assertFalse(result.getPermissions().isEmpty());

        // Cleanup
        permissionService.removeById(testPermission.getId());
    }

    @Test
    void queryPermission_EmptyResult() {
        // Given
        PermissionQuery permissionQuery = new PermissionQuery();
        permissionQuery.setPageNum(1);
        permissionQuery.setPageSize(10);
        permissionQuery.setName("不存在的权限XYZ123");

        // When
        PermissionPageVO result = permissionService.queryPermission(permissionQuery);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getPermissions().isEmpty());
    }

    @Test
    void save_Success() {
        // When
        boolean saved = permissionService.save(testPermission);

        // Then
        assertTrue(saved);
        assertNotNull(testPermission.getId());

        // Cleanup
        permissionService.removeById(testPermission.getId());
    }
}