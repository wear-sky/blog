package com.wearsky.demo.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.user.config.SecurityConfig;
import com.wearsky.demo.user.domain.entity.Role;
import com.wearsky.demo.user.domain.query.RoleQuery;
import com.wearsky.demo.user.domain.vo.RolePageVO;
import com.wearsky.demo.user.service.IRoleService;
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

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRoleService roleService;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("管理员");
        testRole.setCode("ADMIN");
        testRole.setDescription("系统管理员");
        testRole.setCreatedAt(LocalDateTime.now());
        testRole.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void queryRoles_Success() throws Exception {
        // Given
        RolePageVO pageVO = new RolePageVO();
        pageVO.setTotal(1L);
        pageVO.setRoles(Arrays.asList(testRole));

        when(roleService.queryRole(any(RoleQuery.class))).thenReturn(pageVO);

        // When & Then
        mockMvc.perform(get("/user-service/role/query")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void queryRoles_WithoutAdminRole_ShouldReturn403() throws Exception {
        // When & Then
        mockMvc.perform(get("/user-service/role/query"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getRole_Success() throws Exception {
        // Given
        when(roleService.getById(1L)).thenReturn(testRole);

        // When & Then
        mockMvc.perform(get("/user-service/role/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("管理员"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRole_Success() throws Exception {
        // Given
        Role newRole = new Role();
        newRole.setName("普通用户");
        newRole.setCode("USER");
        newRole.setDescription("普通用户");

        when(roleService.save(any(Role.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/user-service/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRole))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRole_Success() throws Exception {
        // Given
        Role updateRole = new Role();
        updateRole.setId(1L);
        updateRole.setName("超级管理员");
        updateRole.setCode("ADMIN");

        when(roleService.updateById(any(Role.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/user-service/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRole))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRole_Success() throws Exception {
        // Given
        when(roleService.removeById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/user-service/role/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}