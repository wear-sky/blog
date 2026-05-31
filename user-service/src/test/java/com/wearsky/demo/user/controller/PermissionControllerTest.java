package com.wearsky.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.user.config.SecurityConfig;
import com.wearsky.demo.user.domain.entity.Permission;
import com.wearsky.demo.user.domain.query.PermissionQuery;
import com.wearsky.demo.user.domain.vo.PermissionPageVO;
import com.wearsky.demo.user.service.IPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
@Import(SecurityConfig.class)
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPermissionService permissionService;

    private Permission testPermission;

    @BeforeEach
    void setUp() {
        testPermission = new Permission();
        testPermission.setId(1L);
        testPermission.setName("用户读取");
        testPermission.setCode("user:read");
        testPermission.setDescription("读取用户信息");
        testPermission.setCreatedAt(LocalDateTime.now());
        testPermission.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void queryPermissions_Success() throws Exception {
        // Given
        PermissionPageVO pageVO = new PermissionPageVO();
        pageVO.setTotal(1L);
        pageVO.setPermissions(Arrays.asList(testPermission));

        when(permissionService.queryPermission(any(PermissionQuery.class))).thenReturn(pageVO);

        // When & Then
        mockMvc.perform(get("/user-service/permission/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void queryPermissions_WithoutAdminRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(get("/user-service/permission/query"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getPermission_Success() throws Exception {
        // Given
        when(permissionService.getById(1L)).thenReturn(testPermission);

        // When & Then
        mockMvc.perform(get("/user-service/permission/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("用户读取"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPermission_Success() throws Exception {
        // Given
        Permission newPermission = new Permission();
        newPermission.setName("用户写入");
        newPermission.setCode("user:write");
        newPermission.setDescription("写入用户信息");

        when(permissionService.save(any(Permission.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/user-service/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPermission))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePermission_Success() throws Exception {
        // Given
        Permission updatePermission = new Permission();
        updatePermission.setId(1L);
        updatePermission.setName("用户读取权限");
        updatePermission.setCode("user:read");

        when(permissionService.updateById(any(Permission.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/user-service/permission")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePermission))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePermission_Success() throws Exception {
        // Given
        when(permissionService.removeById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/user-service/permission/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}