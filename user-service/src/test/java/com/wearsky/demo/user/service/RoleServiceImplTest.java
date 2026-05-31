package com.wearsky.demo.user.service;

import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RoleServiceImplTest {

    @Autowired
    private IRoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setName("测试角色");
        testRole.setCode("TEST");
        testRole.setDescription("测试角色描述");
    }

    @Test
    void queryRole_Success() {
        // Given
        roleService.save(testRole);

        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setPageNum(1);
        roleQuery.setPageSize(10);
        roleQuery.setName("测试");

        // When
        RolePageVO result = roleService.queryRole(roleQuery);

        // Then
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
        assertFalse(result.getRoles().isEmpty());

        // Cleanup
        roleService.removeById(testRole.getId());
    }

    @Test
    void queryRole_EmptyResult() {
        // Given
        RoleQuery roleQuery = new RoleQuery();
        roleQuery.setPageNum(1);
        roleQuery.setPageSize(10);
        roleQuery.setName("不存在的角色XYZ123");

        // When
        RolePageVO result = roleService.queryRole(roleQuery);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRoles().isEmpty());
    }

    @Test
    void save_Success() {
        // When
        boolean saved = roleService.save(testRole);

        // Then
        assertTrue(saved);
        assertNotNull(testRole.getId());

        // Cleanup
        roleService.removeById(testRole.getId());
    }
}